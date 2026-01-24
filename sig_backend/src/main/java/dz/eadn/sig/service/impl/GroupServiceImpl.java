package dz.eadn.sig.service.impl;

import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import dz.eadn.sig.dto.*;
import dz.eadn.sig.mapper.PermissionMapper;
import dz.eadn.sig.mapper.UserMapper;
import dz.eadn.sig.model.*;
import dz.eadn.sig.service.UserLoggedActionsService;
import dz.eadn.sig.util.LoggingInterceptor;
import dz.eadn.sig.util.WITHUUID;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import dz.eadn.sig.exceptions.EntityAlreadyExistsException;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.mapper.CommonModelMapper;
import dz.eadn.sig.mapper.GroupMapper;
import dz.eadn.sig.repository.GroupRepository;
import dz.eadn.sig.repository.PermissionRepository;
import dz.eadn.sig.repository.UserRepository;
import dz.eadn.sig.service.GroupService;
import dz.eadn.sig.service.NotificationMessageService;
import dz.eadn.sig.service.common.impl.CommonServiceImpl;

/**
 * @author Achrouf Abdenour & LAMOUR AMEUR
 *
 */
@Service
public class GroupServiceImpl extends CommonServiceImpl<Group, GroupDto> implements GroupService {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupMapper groupMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private CommonModelMapper<?, ?> cModelMapper;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private NotificationMessagesDto messages;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PermissionMapper permissionMapper;

	@Autowired
	private NotificationMessageService notificationMessageService;

	@Autowired
	private UserLoggedActionsService userLoggedActionsService;

	public GroupServiceImpl() {
		super(Group.class);

	}

	@Override
	public GroupDto save(GroupDto groupDto) {
		GroupDto savedGroup = null;
		Group group = null;
		if (groupDto != null) {
			group = groupRepository.findByName(groupDto.getName());
			if ((group != null && groupDto.getId() == null) || group != null && !groupDto.getId().equals(group.getId())) {
				throw new EntityAlreadyExistsException(
						String.format("Group avec le nom <%s> est toujours exsite ", groupDto.getName()));
			} else if (groupDto.getId() == null) {
				group = groupMapper.dtoToEntity(groupDto);
				group.getUsers().clear();
				group.getPermissions().clear();
			} else{
				if(group.getDescription() != null) group.setDescription(groupDto.getDescription());
				if(groupDto.getLabel() != null){
					group.setName(groupDto.getLabel());
					group.setLabel(groupDto.getLabel());
				}
			}
			if(groupDto.getLabel() != null){
				group.setName(groupDto.getLabel());
			}
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		List<User> deletedUsers = new ArrayList<>();
		List<User> addedUsers = new ArrayList<>();

		List<Permission> deletedPermissions = new ArrayList<>();
		List<Permission> addedPermissions = new ArrayList<>();

		if(groupDto.getUserDtos() != null && !groupDto.getUserDtos().isEmpty()){
			for (UserDto user : groupDto.getUserDtos()) {
				if (user.getIsNew() != null && user.getIsNew()) {

					// fill the list of added users
					addedUsers.add(modelMapper.map(user, User.class));

					group.getUsers().add(userMapper.dtoToEntity(user));
				} else if (user.getToDelete() != null && user.getToDelete()) {

					// fill the list of deleted users
					deletedUsers.add(modelMapper.map(user, User.class));

					group.getUsers().remove(userMapper.dtoToEntity(user));
				}
			}
		}
		if(groupDto.getPermissions() != null && !groupDto.getPermissions().isEmpty()){
			for (PermissionDto permission : groupDto.getPermissions()) {
				if (permission.getIsNew() != null && permission.getIsNew()) {
					addedPermissions.add(modelMapper.map(permission, Permission.class));
					group.getPermissions().add(permissionMapper.dtoToEntity(permission));
				} else if (permission.getToDelete() != null && permission.getToDelete()) {
					deletedPermissions.add(modelMapper.map(permission, Permission.class));
					group.getPermissions().remove(permissionMapper.dtoToEntity(permission));
				}
			}
		}
		group = groupRepository.save(group);
		if(groupDto.getId() != null) {
			List<Map<String, String>> properties = new ArrayList<>();
			Map<String, String> property = new LinkedHashMap<>();
			property.put("attribute", "users");
			property.put("addedValues", addedUsers.stream().map(WITHUUID::getId).collect(Collectors.toList()).toString());
			property.put("deletedValues", deletedUsers.stream().map(WITHUUID::getId).collect(Collectors.toList()).toString());
			Map<String, String> property2 = new LinkedHashMap<>();
			property2.put("attribute", "permissions");
			property2.put("addedValues", addedPermissions.stream().map(WITHUUID::getId).collect(Collectors.toList()).toString());
			property2.put("deletedValues", deletedPermissions.stream().map(WITHUUID::getId).collect(Collectors.toList()).toString());
			properties.add(property);
			properties.add(property2);
			userLoggedActionsService.createAudit(properties, group.getId(), "Group");
		}

		if (group != null) {

			savedGroup = groupMapper.entityToDto(group);

			if (groupDto.getId() == null) {

				String message = String.format(messages.getMessages().get("NM_GROUP_CREATE"), savedGroup.getName(),
						authentication.getName());
				SystemNotification systemNotification = createSystemNotification(Transaction.ADD, savedGroup);

				NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.CREATION,
						message, systemNotification, new ArrayList<>());

				notificationMessageService.sendNotificationMessage(notification);

			} else {

				// notification for deleted users in group

				String message = String.format(messages.getMessages().get("NM_GROUP_REMOVE_USER"), savedGroup.getName(),
						authentication.getName());

				SystemNotification systemNotification = createSystemNotification(Transaction.UPDATE, savedGroup);

				NotificationSimpleDto notification = createNotification(NotificationLevel.SEVERE,
						Operation.SUPPRIMER_UTILISATEUR_DANS_GROUPE, message, systemNotification, deletedUsers);

				if (deletedUsers != null && deletedUsers.size() > 0)
					notificationMessageService.sendNotificationMessage(notification);

				// notification for added users to group

				String message1 = String.format(messages.getMessages().get("NM_GROUP_ADD_USER"), savedGroup.getName(),
						authentication.getName());

				SystemNotification systemNotification1 = createSystemNotification(Transaction.UPDATE, savedGroup);

				NotificationSimpleDto notification1 = createNotification(NotificationLevel.INFO,
						Operation.AJOUTER_UTILISATEUR_DANS_GROUPE, message1, systemNotification1, addedUsers);

				if (addedUsers != null && addedUsers.size() > 0)
					notificationMessageService.sendNotificationMessage(notification1);
			}
		}

		return savedGroup;
	}

	@Override
	public void delete(UUID id) {

		Group group = findById(id);

		if (group == null) {
			throw new EntityNotFoundException("Impossible de supprimer l'entit� " + domainClass.getSimpleName());
		}

		groupRepository.delete(group);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String message = String.format(messages.getMessages().get("NM_GROUP_DELETE"), group.getName(),
				authentication.getName());

		SystemNotification systemNotification = createSystemNotification(Transaction.DELETE,
				modelMapper.map(group, GroupSimpleDto.class));

		NotificationSimpleDto notification = createNotification(NotificationLevel.SEVERE, Operation.SUPPRISSION,
				message, systemNotification, new ArrayList<>());

		notificationMessageService.sendNotificationMessage(notification);
	}

	@Override
	public void deleteAll(Collection<Group> instances) {
		groupRepository.deleteAll(instances);
	}

	@Override
	public PageDto<GroupCompleteDto> getAllGroupsCompleteByPage(Integer page, Integer limit, String sort, String dir) {

		PageDto<GroupDto> pageDto = super.findAllByPage(page, limit, sort, dir);

		PageDto<GroupCompleteDto> groupPage = new PageDto<>();
		groupPage.setContent(cModelMapper.mapList(pageDto.getContent(), GroupCompleteDto.class));
		groupPage.setTotalElements(pageDto.getTotalElements());

		return groupPage;
	}

	@Override
	public PageDto<GroupSimpleDto> findAllGroupsSimpleByFilter(CommonFilter filter, Integer page, Integer limit,
			String sort, String dir) {

		PageDto<GroupDto> pageDto = super.findAllByFilter(filter, page, limit, sort, dir);

		PageDto<GroupSimpleDto> groupPage = new PageDto<>();
		groupPage.setContent(cModelMapper.mapList(pageDto.getContent(), GroupSimpleDto.class));
		groupPage.setTotalElements(pageDto.getTotalElements());

		return groupPage;
	}

	@Override
	public PageDto<GroupCompleteDto> findAllGroupsCompleteByFilter(CommonFilter filter, Integer page, Integer limit,
			String sort, String dir) {

		PageDto<GroupDto> pageDto = super.findAllByFilter(filter, page, limit, sort, dir);

		PageDto<GroupCompleteDto> groupPage = new PageDto<>();
		groupPage.setContent(cModelMapper.mapList(pageDto.getContent(), GroupCompleteDto.class));
		groupPage.setTotalElements(pageDto.getTotalElements());

		return groupPage;
	}

	@Override
	public PageDto<UserSimpleDto> getUsersOfGroup(Group group, Integer page, Integer limit, String sort, String dir) {
		PageDto<UserSimpleDto> pageDto = null;
		if (group != null) {
			pageDto = new PageDto<>();
			Sort sortDir = null;

			if (dir.equals("asc"))
				sortDir = Sort.by(sort).ascending();
			else
				sortDir = Sort.by(sort).descending();

			Pageable pageable = PageRequest.of(page, limit, sortDir);

			Page<User> usersPage = userRepository.findByGroups(group, pageable);
			List<UserSimpleDto> userSimpleDtos = cModelMapper.mapList(usersPage.getContent(), UserSimpleDto.class);
			pageDto.setContent(userSimpleDtos);
			pageDto.setTotalElements(usersPage.getTotalElements());
		}

		return pageDto;
	}

	@Override
	public PageDto<PermissionSimpleDto> getPermissionsOfGroup(Group group, Integer page, Integer limit, String sort,
			String dir) {
		PageDto<PermissionSimpleDto> pageDto = null;
		if (group != null) {
			pageDto = new PageDto<>();
			Sort sortDir = null;

			if (dir.equals("asc"))
				sortDir = Sort.by(sort).ascending();
			else
				sortDir = Sort.by(sort).descending();

			Pageable pageable = PageRequest.of(page, limit, sortDir);

			Page<Permission> permissionsPage = permissionRepository.findByGroups(group, pageable);
			List<PermissionSimpleDto> permissionDtos = cModelMapper.mapList(permissionsPage.getContent(),
					PermissionSimpleDto.class);
			pageDto.setContent(permissionDtos);
			pageDto.setTotalElements(permissionsPage.getTotalElements());
		}

		return pageDto;
	}

	@Override
	public GroupSimpleWithOthersDto getGroupWithOthers(UUID id, String source, Integer page, Integer limit, String sort,
			String dir) {
		Group group = findById(id);
		GroupSimpleWithOthersDto withOthersDto = null;
		PageDto<UserSimpleDto> userPageDto = null;
		PageDto<PermissionSimpleDto> permissionPageDto = null;

		if (group != null) {

			if (source.equals("users"))
				userPageDto = getUsersOfGroup(group, page, limit, sort, dir);
			else if (source.equals("permissions"))
				permissionPageDto = getPermissionsOfGroup(group, page, limit, sort, dir);
			else if (source.equals("all")) {
				userPageDto = getUsersOfGroup(group, page, limit, sort, dir);
				permissionPageDto = getPermissionsOfGroup(group, page, limit, sort, dir);
			}

			withOthersDto = new GroupSimpleWithOthersDto();

			withOthersDto.setId(group.getId());
			withOthersDto.setName(group.getName());
			withOthersDto.setLabel(group.getLabel());
			withOthersDto.setDescription(group.getDescription());
			withOthersDto.setCreatedBy(group.getCreatedBy());
			withOthersDto.setUsers(userPageDto);
			withOthersDto.setPermissions(permissionPageDto);

		}

		return withOthersDto;
	}

	@Override
	public Group findByName(String name) {
		return groupRepository.findByName(name);
	}

	public NotificationSimpleDto createNotification(NotificationLevel level, Operation operation, String message,
			SystemNotification systemNotification, List<User> users) {

		NotificationSimpleDto notification = new NotificationSimpleDto();

		notification.setObject(NotificationObject.Groupe);
		notification.setLevel(level);
		notification.setOperation(operation);
		notification.setDestination("group");
		notification.setMessage(message);
		notification.setSystemNotification(systemNotification);
		notification.setUsers(users);

		return notification;
	}

	public SystemNotification createSystemNotification(Transaction transaction, Object object) {
		SystemNotification systemNotification = new SystemNotification();

		systemNotification.setType("groups");
		systemNotification.setTransaction(transaction);
		systemNotification.setObject(object);

		return systemNotification;
	}
}
