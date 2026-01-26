package dz.eadn.sig.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import dz.eadn.sig.dto.*;
import dz.eadn.sig.service.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import dz.eadn.sig.exceptions.EntityAlreadyExistsException;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.mapper.CommonModelMapper;
import dz.eadn.sig.mapper.UserMapper;
import dz.eadn.sig.model.Group;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.NotificationLevel;
import dz.eadn.sig.model.NotificationObject;
import dz.eadn.sig.model.Operation;
import dz.eadn.sig.model.Settings;
import dz.eadn.sig.model.User;
import dz.eadn.sig.model.UserLayerFilter;
import dz.eadn.sig.model.UserNotification;
import dz.eadn.sig.repository.UserRepository;
import dz.eadn.sig.security.RedisUtil;
import dz.eadn.sig.service.common.impl.CommonServiceImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour & Ameur LAMOUR
 *
 */
@Service
@Slf4j
public class UserServiceImpl extends CommonServiceImpl<User, UserDto> implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	TemplateEngine htmlTemplateEngine;

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private SettingsService settingsService;

	@Autowired
	@org.springframework.context.annotation.Lazy
	private UserNotificationService userNotificationService;

	@Value("${spring.mail.username}")
	private String noReplay;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private NotificationMessagesDto messages;

	@Autowired
	private NotificationMessageService notificationMessageService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private CommonModelMapper<?, ?> cModelMapper;

	public UserServiceImpl() {
		super(User.class);
	}

	@Override
	public User findByUsername(String username) {
		Optional<User> optional = userRepository.findByUsername(username);
		if (!optional.isPresent())
			return null;
		return optional.get();
	}

	@Override
	public User findByEmail(String mail) {
		Optional<User> optional = userRepository.findByEmail(mail);
		if (!optional.isPresent())
			return null;
		return optional.get();
	}

	@Override
	public void delete(UUID id) {

		User user = findById(id);

		if (user == null) {
			throw new EntityNotFoundException("Impossible de supprimer l'entit� " + domainClass.getSimpleName());
		} else {

			if (user.getGroups() != null) {
				for (Group group : user.getGroups()) {
					group.getUsers().remove(user);
				}
			}

			if (user.getLayers() != null) {
				for (Layer layer : user.getLayers()) {
					layer.setUsers(null);
				}
			}

			if (user.getMaps() != null) {
				for (dz.eadn.sig.model.Map map : user.getMaps()) {
					map.setUsers(null);
				}
			}

			if (user.getUserLayerFilters() != null) {
				for (UserLayerFilter layerFilter : user.getUserLayerFilters()) {
					layerFilter.setUser(null);

				}
			}

			if (user.getNotifications() != null) {
				for (UserNotification userNotification : user.getNotifications()) {
					userNotification.setUser(null);
				}
			}

			userRepository.delete(user);

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			String message = String.format(messages.getMessages().get("NM_USER_DELETE"), user.getFirstName(),
					authentication.getName());

			SystemNotification systemNotification = createSystemNotification(Transaction.DELETE,
					modelMapper.map(user, UserDto.class));

			NotificationSimpleDto notification = createNotification(NotificationLevel.SEVERE, Operation.SUPPRISSION,
					message, systemNotification, new ArrayList<>());

			notificationMessageService.sendNotificationMessage(notification);

		}
	}

	@Override
	public void deleteAll(Collection<User> instances) {
		userRepository.deleteAll(instances);
	}

	@Override
	public UserDto save(UserDto userDto) {

		User savedUser = null;

		if (userDto.getId() != null && userDto.getEnabled().equals(false)) {
			redisUtil.srem(userDto.getUsername());
		}

		if (userDto != null) {

			User existByUsername = findByUsername(userDto.getUsername());

			if (existByUsername != null && !existByUsername.getId().equals(userDto.getId())) {
				throw new EntityAlreadyExistsException(
						String.format("Utilisateur avec le nom d'utilisateur %s est exsite", userDto.getUsername()));
			} else {
				User existByEmail = findByEmail(userDto.getUsername());

				if (existByEmail != null && existByEmail.getId() != userDto.getId()) {
					throw new EntityAlreadyExistsException(
							String.format("Utilisateur avec l'email %s est exsite", userDto.getEmail()));
				}

			}

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (userDto.getId() == null) {

				savedUser = userMapper.dtoToEntity(userDto);

				String plainPassword = userDto.getPassword();

				savedUser.setPassword(encoder.encode(plainPassword));

				savedUser = userRepository.save(savedUser);

				UserSimpleDto userSimpleDto = modelMapper.map(savedUser, UserSimpleDto.class);

				String message = String.format(messages.getMessages().get("NM_USER_CREATE"), savedUser.getFirstName(),
						authentication.getName());

				SystemNotification systemNotification = createSystemNotification(Transaction.ADD, userSimpleDto);

				NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.CREATION,
						message, systemNotification, Arrays.asList(savedUser));

				notificationMessageService.sendNotificationMessage(notification);

				Settings settings = settingsService.findByCode("SERVER_ADDRESS");

				if (savedUser != null) {
					// Remove all user group temporary
					savedUser.setGroups(new ArrayList<>());
					updateUserGroups(userDto, savedUser);
					try {
						Map<String, Object> properties = new HashMap<>();

						properties.put("firstName", savedUser.getFirstName());
						properties.put("lastName", savedUser.getLastName());
						properties.put("username", savedUser.getUsername());
						properties.put("password", plainPassword);
						properties.put("server_address", settings.getValue());

						sendMail(savedUser, properties, "newUser", "Creation du compte");

					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			} else {
				updateUserGroups(userDto, existByUsername);
				savedUser = userRepository.save(userMapper.dtoToEntity(userDto));
				String message = String.format(messages.getMessages().get("NM_USER_UPDATE"), savedUser.getFirstName(),
						authentication.getName());

				SystemNotification systemNotification = createSystemNotification(Transaction.UPDATE,
						modelMapper.map(savedUser, UserSimpleDto.class));

				NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.MODIFICATION,
						message, systemNotification, Arrays.asList(savedUser));

				notificationMessageService.sendNotificationMessage(notification);
			}

		}

		return userMapper.entityToDto(savedUser);

	}

	public void updateUserGroups(UserDto userDto, User user) {
		if (userDto.getGroupDtos() != null) {
			List<Group> groupList = cModelMapper.mapList(userDto.getGroupDtos(), Group.class);
			user.getGroups().stream().filter(g -> !groupList.contains(g)).forEach(group -> {
				addDeleteUserGroup(group, user, "delete");
			});
			groupList.forEach(group -> {
				if (user.getGroups().stream().noneMatch(g -> g.getId().equals(group.getId()))) {
					addDeleteUserGroup(group, user, "add");
				}
			});
		}
	}

	public void addDeleteUserGroup(Group group, User user, String action) {
		GroupDto groupDto = new GroupDto();
		groupDto.setUserDtos(new ArrayList<>());
		groupDto.setId(group.getId());
		groupDto.setName(group.getName());
		UserDto userDto1 = new UserDto();
		userDto1.setId(user.getId());
		userDto1.setUsername(user.getUsername());
		if (action.equals("add")) {
			userDto1.setIsNew(true);
		} else {
			userDto1.setToDelete(true);
		}
		groupDto.getUserDtos().add(userDto1);
		groupService.save(groupDto);
	}

	@Override
	public void sendMail(User user, Map<String, Object> properties, String templateName, String subject)
			throws MessagingException, IOException {

		// Prepare the evaluation context
		Context ctx = new Context();
		ctx.setVariables(properties);

		// Prepare message using a Spring helper
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		// Prepare message using a Spring helper
		MimeMessageHelper message = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
				"UTF-8");
		message.setSubject(subject);
		message.setFrom(noReplay);
		message.setTo(user.getEmail());

		// Create the HTML body using Thymeleaf
		String htmlContent = htmlTemplateEngine.process(templateName, ctx);
		message.setText(htmlContent, true /* isHtml */);

		javaMailSender.send(mimeMessage);

		htmlTemplateEngine.clearTemplateCache();

	}

	@Override
	public String generatePassword() {
		String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
		String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
		String numbers = RandomStringUtils.randomNumeric(2);
		String specialChar = RandomStringUtils.random(2, "#$@.%");
		String totalChars = RandomStringUtils.randomAlphanumeric(2);
		String combinedChars = upperCaseLetters.concat(lowerCaseLetters).concat(numbers).concat(specialChar)
				.concat(totalChars);
		List<Character> pwdChars = combinedChars.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
		Collections.shuffle(pwdChars);
		String password = pwdChars.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
				.toString();
		return password;
	}

	public PageDto<UserCompleteDto> getAllSimpleUsersByPage(Integer page, Integer limit, String sort, String dir) {

		PageDto<UserDto> pageDto = super.findAllByPage(page, limit, sort, dir);

		PageDto<UserCompleteDto> userPage = new PageDto<>();
		userPage.setContent(cModelMapper.mapList(pageDto.getContent(), UserCompleteDto.class));
		userPage.setTotalElements(pageDto.getTotalElements());

		return userPage;
	}

	// @Transactional
	@Override
	public void resetPassword(UUID userId, String newPassword) {

		User user = this.findById(userId);

		user.setPassword(encoder.encode(newPassword));

		if (user != null) {
			try {
				Map<String, Object> properties = new HashMap<>();

				properties.put("firstName", user.getFirstName());
				properties.put("lastName", user.getLastName());
				properties.put("password", newPassword);

				sendMail(user, properties, "resetPassword", "reinitialisation du mot de passe");

			} catch (MessagingException | IOException e) {
				log.error(e.getMessage());
			}

			user = userRepository.save(user);

			UserDto savedUser = modelMapper.map(user, UserDto.class);

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			String message = String.format(messages.getMessages().get("NM_USER_RESET_PASSWORD"),
					savedUser.getFirstName(), authentication.getName());

			SystemNotification systemNotification = createSystemNotification(Transaction.UPDATE, savedUser);

			NotificationSimpleDto notification = createNotification(NotificationLevel.WARNING,
					Operation.RENITIALISATION_MOT_PASSE, message, systemNotification, Arrays.asList(user));

			notificationMessageService.sendNotificationMessage(notification);

		}

	}

	@Override
	public boolean isAdministrateur(String username) {
		User user = this.findByUsername(username);

		if (user != null) {
			for (Group group : user.getGroups()) {
				if (group.getName().equals("ROLE_ADMIN")) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public PageDto<UserSimpleDto> findAllUsersSimpleByFilter(CommonFilter filter, Integer page, Integer limit,
			String sort, String dir) {

		PageDto<UserDto> pageDto = super.findAllByFilter(filter, page, limit, sort, dir);

		PageDto<UserSimpleDto> userPage = new PageDto<>();
		userPage.setContent(cModelMapper.mapList(pageDto.getContent(), UserSimpleDto.class));
		userPage.setTotalElements(pageDto.getTotalElements());

		return userPage;

	}

	@Override
	public PageDto<UserCompleteDto> findAllUsersCompleteByFilter(CommonFilter filter, Integer page, Integer limit,
			String sort, String dir) {

		PageDto<UserDto> pageDto = super.findAllByFilter(filter, page, limit, sort, dir);

		PageDto<UserCompleteDto> userPage = new PageDto<>();
		userPage.setContent(cModelMapper.mapList(pageDto.getContent(), UserCompleteDto.class));
		userPage.setTotalElements(pageDto.getTotalElements());

		return userPage;
	}

	@Override
	public PageDto<UserCompleteDto> getAllUsersCompleteByPage(Integer page, Integer limit, String sort, String dir) {
		PageDto<UserDto> pageDto = super.findAllByPage(page, limit, sort, dir);

		PageDto<UserCompleteDto> userPage = new PageDto<>();
		userPage.setContent(cModelMapper.mapList(pageDto.getContent(), UserCompleteDto.class));
		userPage.setTotalElements(pageDto.getTotalElements());

		return userPage;
	}

	@Override
	public ProfileDto getCurrentUser(String username, Integer page, Integer limit, String sort, String dir) {
		ProfileDto profileDto = null;
		User user = findByUsername(username);
		if (user != null) {
			profileDto = modelMapper.map(user, ProfileDto.class);
			List<String> claims = new ArrayList<String>();
			List<LayerSimpleDto> layers = profileDto.getLayers();
			List<MapSimpleDto> maps = profileDto.getMaps();

			if (user.getGroups() != null) {
				for (Group group : user.getGroups()) {
					claims.addAll(group.getPermissions().stream().map(p -> p.getName()).collect(Collectors.toList()));
					if (group.getName().equals("ROLE_ADMIN")) {
						claims.add(group.getName());
					}

					// load the layers and maps shared with user
					if (group.getLayers() != null) {

						layers.addAll(cModelMapper.mapList(group.getLayers(), LayerSimpleDto.class));
						layers = layers.stream().filter(distinctByKey(u -> u.getSlug())).collect(Collectors.toList());

						profileDto.getLayers().clear();
						profileDto.setLayers(layers);
					}

					if (group.getMaps() != null) {

						maps.addAll(cModelMapper.mapList(group.getMaps(), MapSimpleDto.class));
						maps = maps.stream().filter(distinctByKey(u -> u.getSlug())).collect(Collectors.toList());

						profileDto.getMaps().clear();
						profileDto.setMaps(maps);
					}

				}
			}
			profileDto.setClaims(claims);

			PageDto<UserNotificationDto> pageDto = userNotificationService.findAllNotifications(user, false, page,
					limit, sort, dir);
			profileDto.setNotificationDtos(pageDto);

		}
		return profileDto;
	}

	public NotificationSimpleDto createNotification(NotificationLevel level, Operation operation, String message,
			SystemNotification systemNotification, List<User> users) {

		NotificationSimpleDto notification = new NotificationSimpleDto();

		notification.setObject(NotificationObject.Utilisateur);
		notification.setLevel(level);
		notification.setOperation(operation);
		notification.setDestination("user");
		notification.setMessage(message);
		notification.setSystemNotification(systemNotification);
		notification.setUsers(users);

		return notification;
	}

	public SystemNotification createSystemNotification(Transaction transaction, Object object) {
		SystemNotification systemNotification = new SystemNotification();

		systemNotification.setType("users");
		systemNotification.setTransaction(transaction);
		systemNotification.setObject(object);

		return systemNotification;
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

}
