package dz.eadn.sig.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import dz.eadn.sig.service.*;
import dz.eadn.sig.util.WITHUUID;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import dz.eadn.sig.dto.FilterDto;
import dz.eadn.sig.dto.NotificationMessagesDto;
import dz.eadn.sig.dto.NotificationSimpleDto;
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.dto.ShareFilterDto;
import dz.eadn.sig.dto.SystemNotification;
import dz.eadn.sig.dto.Transaction;
import dz.eadn.sig.dto.UserDto;
import dz.eadn.sig.dto.UserSimpleDtoWithFilterProjection;
import dz.eadn.sig.exceptions.GlobalException;
import dz.eadn.sig.mapper.CommonModelMapper;
import dz.eadn.sig.mapper.FilterMapper;
import dz.eadn.sig.mapper.UserMapper;
import dz.eadn.sig.model.Filter;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.NotificationLevel;
import dz.eadn.sig.model.NotificationObject;
import dz.eadn.sig.model.Operation;
import dz.eadn.sig.model.User;
import dz.eadn.sig.model.UserLayerFilter;
import dz.eadn.sig.repository.FilterRepository;
import dz.eadn.sig.repository.LayerRepository;
import dz.eadn.sig.repository.UserLayerFiltersRepository;
import dz.eadn.sig.repository.UserRepository;
import dz.eadn.sig.service.common.impl.CommonServiceImpl;

@Service
public class FilterServiceImpl extends CommonServiceImpl<Filter, FilterDto> implements FilterService {

	@Autowired
	private FilterMapper filterMapper;

	@Autowired
	private UserMapper userMapper;

	private UserRepository userRepository;

	private LayerRepository layerRepository;

	private FilterRepository filterRepository;

	private LayerService layerService;

	private UserLayerFiltersRepository userLayerFiltersRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private NotificationMessagesDto messages;

	@Autowired
	private NotificationMessageService notificationMessageService;

	@Autowired
	private UserLoggedActionsService userLoggedActionsService;

	@Autowired
	private CommonModelMapper<?, ?> cModelMapper;

	@Autowired
	private ModelMapper modelMapper;

	public FilterServiceImpl(UserRepository userRepository, LayerRepository layerRepository,
			FilterRepository filterRepository, LayerService layerService,
			UserLayerFiltersRepository userLayerFiltersRepository) {
		super(Filter.class);
		this.userRepository = userRepository;
		this.layerRepository = layerRepository;
		this.filterRepository = filterRepository;
		this.layerService = layerService;
		this.userLayerFiltersRepository = userLayerFiltersRepository;
	}

	@Override
	public FilterDto addFilter(FilterDto filterdto, String layerSlug) {
		layerService.CheckIfUserHasPrivilegeOnLayer(layerSlug, null, null, "write");
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
		Filter filter = filterMapper.dtoToEntity(filterdto);
		filterRepository.save(filter);
		Layer layer = layerRepository.findBySlug(layerSlug);
		UserLayerFilter userLayerFilter = new UserLayerFilter(user.get(), layer, filter, filter.getId());
		userLayerFiltersRepository.save(userLayerFilter);

		// send Notification

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String message = String.format(messages.getMessages().get("NM_FILTER_CREATE"), filter.getName(),
				authentication.getName());

		SystemNotification systemNotification = createSystemNotification(Transaction.ADD, filter);

		NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.CREATION, message,
				systemNotification, new ArrayList<>());

		notificationMessageService.sendNotificationMessage(notification);

		return filterMapper.mapEntityToDto(filter, user.get().getId());
	}

	@Override
	public FilterDto updateFilter(FilterDto filter) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
		List<UserLayerFilter> userLayerFilterList = userLayerFiltersRepository
				.findUserLayerFilterByFilterIdAndUser(filter.getId(), user.get());
		if (userLayerFilterList.size() > 0) {
			List<Filter> filterList = filterRepository.findFilterByUserLayerFiltersIn(userLayerFilterList);
			if (filterList.size() > 0 && userLayerFilterList.get(0).getFilterClonedFrom() != null) {
				filterList.get(0).setFilterConfig(filter.getFilterConfig());
				filterList.get(0).setName(filter.getName());
				filterList.get(0).setDescription(filter.getDescription());
				filterList.get(0).setLastModifiedDate(new Date());
				filterRepository.save(filterList.get(0));

				// send Notification

				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

				String message = String.format(messages.getMessages().get("NM_FILTER_UPDATE"), filter.getName(),
						authentication.getName());

				SystemNotification systemNotification = createSystemNotification(Transaction.UPDATE, filter);

				NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.MODIFICATION,
						message, systemNotification, new ArrayList<>());

				notificationMessageService.sendNotificationMessage(notification);

				return filterMapper.entityToDto(filterList.get(0));
			} else {
				throw new GlobalException("échec de l'opération de mise à jour du filtre");
			}
		} else {
			throw new GlobalException("échec de l'opération de mise à jour du filtre");
		}
	}

	@Override
	public List<UserLayerFilter> getFilterByLayer(Layer layer, Boolean distinct) {
		if(distinct){
			return userLayerFiltersRepository.findDistinctByLayer(layer);
		}
		return userLayerFiltersRepository.findUserLayerFilterByLayer(layer);
	}

	@Override
	public void deleteFilter(UUID filterId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Filter exitFilter = findById(filterId);

		if (exitFilter != null && (authentication.getName().equals(exitFilter.getCreatedBy())
				|| userService.isAdministrateur(authentication.getName()))) {

			filterRepository.deleteById(filterId);

			String message = String.format(messages.getMessages().get("NM_FILTER_DELETE"), exitFilter.getName(),
					authentication.getName());

			SystemNotification systemNotification = createSystemNotification(Transaction.DELETE, null);

			NotificationSimpleDto notification = createNotification(NotificationLevel.SEVERE, Operation.SUPPRISSION,
					message, systemNotification, new ArrayList<>());
			
			notificationMessageService.sendNotificationMessage(notification);
		} else {
			throw new GlobalException("échec de l'opération de la suppression du filtre");
		}
	}

	@Override
	public PageDto<FilterDto> getUserFilters(Integer page, Integer limit, String sort, String dir) {
		PageDto<FilterDto> pageDto = new PageDto<>();
		Pageable pageable = null;
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
		List<UserLayerFilter> userLayerFilterList = userLayerFiltersRepository.findUserLayerFilterByUser(user.get());
		if (dir.equals("asc"))
			pageable = PageRequest.of(page, limit, Sort.by(sort).ascending());
		else
			pageable = PageRequest.of(page, limit, Sort.by(sort).descending());

		Page<Filter> filterPage = filterRepository.findFilterByUserLayerFiltersIn(pageable, userLayerFilterList);

		pageDto.setContent(filterMapper.entitysToDtos(filterPage.getContent(), user.get().getId()));
		pageDto.setTotalElements(filterPage.getTotalElements());
		return pageDto;
	}

	@Override
	@Transactional
	public void shareFilter(ShareFilterDto shareFilterDto) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Layer layer = layerRepository.findBySlug(shareFilterDto.getLayerSlug());
		boolean isPermitted;
		List<UserDto> addedUsers = new ArrayList<>();

		for (UserDto user : shareFilterDto.getUsers()) {
			isPermitted = false;
			if (user.getToDelete() != null && user.getToDelete()) {
				userLayerFiltersRepository.deleteUserLayerFilterByFilterIdAndUserId(shareFilterDto.getFilter().getId(),
						user.getId());
				SystemNotification systemNotification = createSystemNotification(Transaction.DELETE,
						shareFilterDto.getFilter());

				String message = String.format(messages.getMessages().get("NM_FILTER_UNSHARE"),
						shareFilterDto.getFilter().getName(), authentication.getName());

				NotificationSimpleDto notifDeleted = createNotification(NotificationLevel.SEVERE, Operation.DEPARTAGE,
						message, systemNotification, Arrays.asList(modelMapper.map(user, User.class)));

				notificationMessageService.sendNotificationMessage(notifDeleted);

			} else if (user.getIsNew() != null && user.getIsNew()) {
				// Check if the layer shared with this user
				if (userService.isAdministrateur(user.getUsername())) {
					isPermitted = true;
				} else if (layer.getUsers().stream().filter(u -> u.getId().equals(user.getId()))
						.collect(Collectors.toList()).size() > 0) {
					isPermitted = true;
				} else {
					// Check if the layer shared with user's groups
					for (int i = 0; i < layer.getGroups().size(); i++) {
						if (userMapper.dtoToEntity(user).getGroups().contains(layer.getGroups().get(i))) {
							isPermitted = true;
						}
					}
				}
				if (isPermitted && userLayerFiltersRepository.countUserLayerFilterByFilterClonedFromAndUser(
						shareFilterDto.getFilter().getId(), userMapper.dtoToEntity(user)) == 0) {
					// if user have all privilege on filter
					if (shareFilterDto.getUsersWithPermission().get(user.getEmail()) != null
							&& shareFilterDto.getUsersWithPermission().get(user.getEmail())) {
						UUID filterId = shareFilterDto.getFilter().getId();
						cloneFilter(shareFilterDto, user, layer);
						shareFilterDto.getFilter().setId(filterId);
					} else {
						UserLayerFilter userLayerFilter = new UserLayerFilter(userMapper.dtoToEntity(user), layer,
								filterMapper.dtoToEntity(shareFilterDto.getFilter()), null);
						userLayerFiltersRepository.save(userLayerFilter);
					}
					addedUsers.add(user);
					// prepare the notification for new users
					SystemNotification systemNotification = createSystemNotification(Transaction.ADD,
							shareFilterDto.getFilter());

					String message = String.format(messages.getMessages().get("NM_FILTER_SHARE"),
							shareFilterDto.getFilter().getName(), authentication.getName());

					NotificationSimpleDto notifAdded = createNotification(NotificationLevel.INFO, Operation.PARTAGE,
							message, systemNotification, Arrays.asList(modelMapper.map(user, User.class)));

					notificationMessageService.sendNotificationMessage(notifAdded);
				}

			}
		}

		//Audit share filter action
		List<java.util.Map<String, String>> properties = new ArrayList<>();
		java.util.Map<String, String> property = new LinkedHashMap<>();
		property.put("attribute", "users");
		property.put("addedValues", addedUsers.stream().map(WITHUUID::getId).collect(Collectors.toList()).toString());
		property.put("deletedValues", shareFilterDto.getUsers().stream().filter(u -> u.getToDelete() != null &&  u.getToDelete()).collect(Collectors.toList()).toString());
		properties.add(property);
		userLoggedActionsService.createAudit(properties, shareFilterDto.getFilter().getId(), "Filter");

	}

	public void cloneFilter(ShareFilterDto shareFilterDto, UserDto user, Layer layer) {
		userLayerFiltersRepository.deleteUserLayerFilterByFilterIdAndUserId(shareFilterDto.getFilter().getId(),
				user.getId());
		UUID clonedFilterId = shareFilterDto.getFilter().getId();
		shareFilterDto.getFilter().setId(null);
		Filter filter = filterMapper.dtoToEntity(shareFilterDto.getFilter());
		filter.setUserLayerFilters(null);
		filterRepository.save(filter);
		UserLayerFilter userLayerFilter = new UserLayerFilter(userMapper.dtoToEntity(user), layer, filter,
				clonedFilterId);
		userLayerFiltersRepository.save(userLayerFilter);
	}

	@Override
	public PageDto<UserSimpleDtoWithFilterProjection> getUsersBelongsToFilter(UUID filterId, Integer page,
			Integer limit, String sort, String dir) {

		PageDto<UserSimpleDtoWithFilterProjection> pageDto = new PageDto<>();

		Pageable pageable = PageRequest.of(page, limit);
		;
		Page<UserSimpleDtoWithFilterProjection> usersPage = filterRepository.getUsersBelongsToUser(filterId, pageable);
		List<UserSimpleDtoWithFilterProjection> userSimpleDtos = cModelMapper.mapList(usersPage.getContent(),
				UserSimpleDtoWithFilterProjection.class);
		pageDto.setContent(userSimpleDtos);
		pageDto.setTotalElements(usersPage.getTotalElements());
		return pageDto;
	}

	public NotificationSimpleDto createNotification(NotificationLevel level, Operation operation, String message,
			SystemNotification systemNotification, List<User> users) {

		NotificationSimpleDto notification = new NotificationSimpleDto();

		notification.setObject(NotificationObject.Filtre);
		notification.setLevel(level);
		notification.setOperation(operation);
		notification.setDestination("filter");
		notification.setMessage(message);
		notification.setSystemNotification(systemNotification);
		notification.setUsers(users);

		return notification;
	}

	public SystemNotification createSystemNotification(Transaction transaction, Object object) {
		SystemNotification systemNotification = new SystemNotification();

		systemNotification.setType("filters");
		systemNotification.setTransaction(transaction);
		systemNotification.setObject(object);

		return systemNotification;
	}

}
