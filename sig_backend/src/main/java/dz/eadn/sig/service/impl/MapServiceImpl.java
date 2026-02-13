
package dz.eadn.sig.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import dz.eadn.sig.dto.*;
import dz.eadn.sig.repository.LayerRepository;
import dz.eadn.sig.service.*;
import dz.eadn.sig.util.WITHUUID;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.hibernate.spatial.JTSGeometryType;
import org.hibernate.spatial.dialect.postgis.PGGeometryTypeDescriptor;
import org.locationtech.jts.geom.Geometry;
import org.modelmapper.ModelMapper;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import dz.eadn.sig.exceptions.EntityAlreadyExistsException;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.mapper.CommonModelMapper;
import dz.eadn.sig.mapper.GroupMapper;
import dz.eadn.sig.mapper.MapMapper;
import dz.eadn.sig.mapper.UserMapper;
import dz.eadn.sig.model.Group;
import dz.eadn.sig.model.Map;
import dz.eadn.sig.model.MapLayer;
import dz.eadn.sig.model.NotificationLevel;
import dz.eadn.sig.model.NotificationObject;
import dz.eadn.sig.model.Operation;
import dz.eadn.sig.model.Privacy;
import dz.eadn.sig.model.User;
import dz.eadn.sig.model.Theme;
import dz.eadn.sig.repository.GroupRepository;
import dz.eadn.sig.repository.MapRepository;
import dz.eadn.sig.repository.UserRepository;
import dz.eadn.sig.service.common.impl.CommonServiceImpl;
import dz.eadn.sig.util.SearchCriteria;
import dz.eadn.sig.util.Utils;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour & A.LAMOUR
 *
 */
@Service
@Slf4j
public class MapServiceImpl extends CommonServiceImpl<dz.eadn.sig.model.Map, MapDto> implements MapService {

	@Value("${geoserver.workspace}")
	private String workspace;

	@Value("${geoserver.rest.guestRole}")
	private String geoserverGuestRole;

	@Autowired
	private MapRepository mapRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private LayerRepository layerRepository;

	@Autowired
	private GroupService groupService;

	@Autowired
	private MapMapper mapMapper;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private NotificationMessagesDto messages;

	@Autowired
	private GeoserverSecurityService geoserverSecurityService;

	@Autowired
	private CommonModelMapper<?, ?> cModelMapper;

	@Autowired
	private NotificationMessageService notificationMessageService;

	@Autowired
	private UserLoggedActionsServiceImpl userLoggedActionsService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private MapLayerService mapLayerService;

	@Autowired
	private GroupMapper groupMapper;

	@Autowired
	private LayerStylesService layerStylesService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public MapServiceImpl() {
		super(dz.eadn.sig.model.Map.class);
	}

	public void addRule(String layerSlug) {
		HashMap<String, String> rules = new HashMap<>();
		rules.put(workspace + "." + layerSlug + ".r", geoserverGuestRole);
		try {
			geoserverSecurityService.addLayersRules(rules);
			rules.clear();
		} catch (FeignException e) {
			if (e.status() != 1) {
				log.error("Error adding GeoServer security rule", e);
				rules.clear();
			}
		}
	}

	public MapSimpleDto shareMap(UUID id, ShareMapWithOthers sharedMap) {

		dz.eadn.sig.model.Map map = this.findById(id);

		MapSimpleDto mapSimpleDto = null;

		Privacy oldPrivacy = map.getPrivacy();

		if (map != null) {

			if ((sharedMap.getPrivacy().equals(Privacy.PRIVATE) || sharedMap.getPrivacy().equals(Privacy.ARCHIVED))
					&& (map.getPrivacy().equals(Privacy.PUBLIC_WITH_LINK) || map.getPrivacy().equals(Privacy.PUBLIC))) {
				map.getLayers().forEach(l -> {
					try {
						// Set map as private if and only if it shared with one map
						if (l.getMap().getSlug().equals(map.getSlug()) && l.getLayer().getMaps().stream()
								.filter(m -> m.getMap().getPrivacy().equals(Privacy.PUBLIC)
										|| m.getMap().getPrivacy().equals(Privacy.PUBLIC_WITH_LINK))
								.collect(Collectors.toList()).size() == 1) {
							geoserverSecurityService.deleteLayersRules(workspace + "." + l.getLayer().getSlug() + ".r");
						}
					} catch (FeignException e) {
						if (e.status() != 1) {
							log.error("Error removing GeoServer security rule for layer: " + l.getLayer().getSlug(), e);
						}
					}
				});
			}

			if ((sharedMap.getPrivacy().equals(Privacy.PUBLIC)
					|| sharedMap.getPrivacy().equals(Privacy.PUBLIC_WITH_LINK))) {
				HashMap<String, String> rules = new HashMap<>();
				// Set map as public
				map.getLayers().forEach(l -> {
					addRule(l.getLayer().getSlug());
				});
			}

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			List<User> deletedUsers = new ArrayList<>();
			List<User> addedUsers = new ArrayList<>();

			if (sharedMap.getUsers() != null) {

				sharedMap.getUsers().forEach(user -> {
					if (user.getIsNew() != null && user.getIsNew()) {

						// fill the list of added users
						addedUsers.add(modelMapper.map(user, User.class));

						map.getUsers().add(userMapper.dtoToEntity(user));
					} else if (user.getToDelete() != null && user.getToDelete()) {

						// fill the list of deleted users
						deletedUsers.add(modelMapper.map(user, User.class));

						map.getUsers().remove(userMapper.dtoToEntity(user));
					}
				});

			}

			if (sharedMap.getGroups() != null) {

				for (GroupDto groupSimpleDto : sharedMap.getGroups()) {

					if (groupSimpleDto.getIsNew() != null && groupSimpleDto.getIsNew()) {

						// fill the list of added users

						Group group = groupService.findById(groupSimpleDto.getId());
						if (group != null) {
							addedUsers.addAll(group.getUsers());
						}

						map.getGroups().add(groupMapper.dtoToEntity(groupSimpleDto));
					} else if (groupSimpleDto.getToDelete() != null && groupSimpleDto.getToDelete()) {

						// fill the list of deleted users

						Group group = groupService.findById(groupSimpleDto.getId());
						if (group != null) {
							deletedUsers.addAll(group.getUsers());
						}

						map.getGroups().remove(groupMapper.dtoToEntity(groupSimpleDto));
					}
				}
			}

			if (sharedMap.getPrivacy() != null) {
				map.setPrivacy(sharedMap.getPrivacy());
			}

			Map savedMap = mapRepository.save(map);

			// Audit share map action
			List<java.util.Map<String, String>> properties = new ArrayList<>();
			java.util.Map<String, String> property = new LinkedHashMap<>();
			property.put("attribute", "users");
			property.put("addedValues", sharedMap.getUsers().stream().filter(u -> u.getIsNew() != null && u.getIsNew())
					.map(WITHUUID::getId).collect(Collectors.toList()).toString());
			property.put("deletedValues", sharedMap.getUsers().stream()
					.filter(u -> u.getToDelete() != null && u.getToDelete()).collect(Collectors.toList()).toString());
			java.util.Map<String, String> property2 = new LinkedHashMap<>();
			property2.put("attribute", "groups");
			property2.put("addedValues",
					sharedMap.getGroups().stream().filter(g -> g.getIsNew() != null && g.getIsNew())
							.map(WITHUUID::getId).collect(Collectors.toList()).toString());
			property2.put("deletedValues",
					sharedMap.getGroups().stream().filter(g -> g.getToDelete() != null && g.getToDelete())
							.map(WITHUUID::getId).collect(Collectors.toList()).toString());
			properties.add(property);
			properties.add(property2);
			userLoggedActionsService.createAudit(properties, id, "Map");

			mapSimpleDto = modelMapper.map(savedMap, MapSimpleDto.class);

			if (!sharedMap.getPrivacy().equals(oldPrivacy)) {

				String privacy = "";
				switch (sharedMap.getPrivacy()) {
					case PRIVATE: {
						privacy = "Priv�";
						break;
					}
					case PUBLIC: {
						privacy = "Publique";
						break;
					}
					case PUBLIC_WITH_LINK: {
						privacy = "Partag� avec lien";
						break;
					}
					default:
						break;
				}

				SystemNotification systemNotification = createSystemNotification(Transaction.UPDATE, mapSimpleDto);

				String message = String.format(messages.getMessages().get("NM_MAP_PRIVACY"), mapSimpleDto.getName(),
						authentication.getName(), privacy);

				NotificationSimpleDto notifPrivacy = createNotification(NotificationLevel.INFO,
						Operation.CHANGEMENT_PRIVACY, message, systemNotification, addedUsers);

				notificationMessageService.sendNotificationMessage(notifPrivacy);

			}

			// prepare the notification for deleted users
			if (deletedUsers != null && deletedUsers.size() > 0) {

				SystemNotification systemNotification = createSystemNotification(Transaction.DELETE, mapSimpleDto);

				String message = String.format(messages.getMessages().get("NM_MAP_UNSHARE"), mapSimpleDto.getName(),
						authentication.getName());

				NotificationSimpleDto notifDeleted = createNotification(NotificationLevel.SEVERE, Operation.DEPARTAGE,
						message, systemNotification, deletedUsers);

				notificationMessageService.sendNotificationMessage(notifDeleted);

			}

			// prepare the notification for new users
			if (addedUsers != null && addedUsers.size() > 0) {

				SystemNotification systemNotification = createSystemNotification(Transaction.ADD, mapSimpleDto);

				String message = String.format(messages.getMessages().get("NM_MAP_SHARE"), mapSimpleDto.getName(),
						authentication.getName());

				NotificationSimpleDto notifAdded = createNotification(NotificationLevel.INFO, Operation.PARTAGE,
						message, systemNotification, addedUsers);

				notificationMessageService.sendNotificationMessage(notifAdded);
			}
		}

		return mapSimpleDto;

	}

	@Override
	public void delete(UUID id) {

		dz.eadn.sig.model.Map map = findById(id);

		if (map == null) {
			throw new EntityNotFoundException("Impossible de supprimer l'entité " + domainClass.getSimpleName());
		}

		// Delete all layer styles on this map from the geoserver
		layerStylesService.deleteStylesByMap(map);

		mapRepository.delete(map);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String message = String.format(messages.getMessages().get("NM_MAP_DELETE"), map.getName(),
				authentication.getName());

		SystemNotification systemNotification = createSystemNotification(Transaction.DELETE,
				modelMapper.map(map, MapSimpleDto.class));

		NotificationSimpleDto notification = createNotification(NotificationLevel.SEVERE, Operation.SUPPRISSION,
				message, systemNotification, new ArrayList<>());

		notificationMessageService.sendNotificationMessage(notification);
	}

	public ReferencedEnvelope getMapBBOX(dz.eadn.sig.model.Map map) {
		if (map.getLayers() == null || map.getLayers().isEmpty()) {
			return null;
		}
		List<UUID> layerIds = map.getLayers().stream()
				.map(ml -> ml.getLayer().getId())
				.collect(Collectors.toList());

		Geometry geom = null;
		try {
			geom = (Geometry) entityManager.createNativeQuery(
					"select st_setsrid(st_extent(entityelem0_.geom),4326) as geom from sig.entity_element entityelem0_ where entityelem0_.layer_entity_element in (:layerIds)")
					.setParameter("layerIds", layerIds)
					.unwrap(org.hibernate.query.NativeQuery.class)
					.addScalar("geom", new JTSGeometryType(PGGeometryTypeDescriptor.INSTANCE)).getSingleResult();
		} catch (Exception e) {
			log.error("Error calculating BBOX for map: " + map.getId(), e);
			return null;
		}

		CoordinateReferenceSystem crs = null;
		try {
			crs = CRS.decode("EPSG:4326");
		} catch (FactoryException e) {

		}

		return new ReferencedEnvelope(geom.getEnvelopeInternal().getMinX(), geom.getEnvelopeInternal().getMaxX(),
				geom.getEnvelopeInternal().getMinY(), geom.getEnvelopeInternal().getMaxY(), crs);

	}

	@Override
	public long count() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userName = authentication.getName();

		TypedQuery<Long> query = entityManager.createQuery(
				"SELECT count(map) from Map map WHERE (select user from User user where user.username = :username) MEMBER OF map.users",
				Long.class);

		query.setParameter("username", userName);
		return query.getSingleResult();
	}

	@Override
	public List<dz.eadn.sig.model.Map> findAll(Integer page, Integer limit, String sort, String dir) {
		return super.findAll(page, limit, sort, dir);
	}

	public PageDto<MapSimpleDto> getMapsByPage(Integer page, Integer limit, String sort, String dir) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Map> cq = cb.createQuery(Map.class);
		Root<Map> rootMap = cq.from(Map.class);

		Join<Map, User> MapjoinUser = rootMap.join("users", JoinType.LEFT);
		Join<Map, Group> MapjoinGroup = rootMap.join("groups", JoinType.LEFT);

		TypedQuery<Map> typedQuery = null;

		PageDto<MapSimpleDto> pageDto = new PageDto<>();

		Pageable pageable = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
			String username = authentication.getName();

			if (userService.isAdministrateur(username)) {
				if (dir.equals("asc"))
					pageable = PageRequest.of(page, limit, Sort.by(sort).ascending());
				else
					pageable = PageRequest.of(page, limit, Sort.by(sort).descending());

				Page<dz.eadn.sig.model.Map> mapPage = mapRepository.findAll(pageable);

				List<MapSimpleDto> mapTableDtos = cModelMapper.mapList(mapPage.getContent(), MapSimpleDto.class);

				pageDto.setContent(mapTableDtos);
				pageDto.setTotalElements(mapPage.getTotalElements());

			} else {
				User user = userService.findByUsername(username);

				List<UUID> groupIds = new ArrayList<>();

				for (Group group : user.getGroups()) {
					groupIds.add(group.getId());
				}

				typedQuery = entityManager.createQuery(cq.select(rootMap).distinct(true).where(
						cb.or(cb.equal(MapjoinUser.get("id"), user.getId()), MapjoinGroup.get("id").in(groupIds))));

				int total = typedQuery.getResultList().size();

				if (limit != -1)
					typedQuery.setFirstResult(page * limit).setMaxResults(limit);

				List<Map> maps = typedQuery.getResultList();

				List<MapSimpleDto> mapTableDtos = cModelMapper.mapList(maps, MapSimpleDto.class);

				pageDto.setContent(mapTableDtos);
				pageDto.setTotalElements(total);

			}

		}

		return pageDto;

	}

	public PageDto<MapSimpleDto> findMapsByFilter(CommonFilter filter, Integer page, Integer limit, String sort,
			String dir) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Map> cq = cb.createQuery(Map.class);
		Root<Map> root = cq.from(Map.class);

		Join<Map, User> MapjoinUser = root.join("users", JoinType.LEFT);
		Join<Map, Group> MapjoinGroup = root.join("groups", JoinType.LEFT);

		List<Predicate> predicates = new ArrayList<>();
		PageDto<MapSimpleDto> pageDto = new PageDto<>();

		Predicate deletePre, commonsPre, ownerPre, finalPre = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		if (!userService.isAdministrateur(username)) {

			User user = userService.findByUsername(username);

			List<UUID> groupIds = new ArrayList<>();

			for (Group group : user.getGroups()) {
				groupIds.add(group.getId());
			}

			ownerPre = cb.or(cb.equal(MapjoinUser.get("id"), user.getId()), MapjoinGroup.get("id").in(groupIds));
			predicates.add(ownerPre);
		}

		if (dir.equals("asc"))
			cq.orderBy(cb.asc(root.get(sort)));
		else
			cq.orderBy(cb.desc(root.get(sort)));

		deletePre = cb.equal(root.get("deleted"), false);

		for (SearchCriteria criteria : filter.getRules()) {
			if (criteria.getValue() != "")
				predicates.add(findByProperty(criteria, cb, root));
		}

		if (filter.getCondition().equals("or"))
			commonsPre = cb.or(predicates.toArray(new Predicate[] {}));
		else
			commonsPre = cb.and(predicates.toArray(new Predicate[] {}));

		finalPre = cb.and(commonsPre, deletePre);

		TypedQuery<Map> typedQuery = entityManager.createQuery(cq.select(root).distinct(true).where(finalPre));

		int total = typedQuery.getResultList().size();

		if (limit != -1)
			typedQuery.setFirstResult(page * limit).setMaxResults(limit);

		List<Map> maps = typedQuery.getResultList();

		pageDto.setContent(cModelMapper.mapList(maps, MapSimpleDto.class));
		pageDto.setTotalElements(total);

		return pageDto;

	}

	@Override
	public PageDto<MapSimpleDto> getAllPublicMaps(Integer page, Integer limit) {
		PageDto<MapSimpleDto> pageDto = new PageDto<>();
		Pageable pageable = PageRequest.of(page, limit);
		Page<dz.eadn.sig.model.Map> pagedResult = mapRepository.findByPrivacy(Privacy.PUBLIC, pageable);
		List<MapSimpleDto> dtos = cModelMapper.mapList(pagedResult.getContent(), MapSimpleDto.class);
		pageDto.setContent(dtos);
		pageDto.setTotalElements(pagedResult.getTotalElements());
		return pageDto;
	}

	@Override
	public PageDto<MapSimpleDto> getAllPublicMaps(Integer page, Integer limit, String name) {
		PageDto<MapSimpleDto> pageDto = new PageDto<>();
		Pageable pageable = PageRequest.of(page, limit);
		Page<dz.eadn.sig.model.Map> pagedResult = mapRepository.findByPrivacyAndIgnoreCaseName(Privacy.PUBLIC, name,
				pageable);
		List<MapSimpleDto> dtos = cModelMapper.mapList(pagedResult.getContent(), MapSimpleDto.class);
		pageDto.setContent(dtos);
		pageDto.setTotalElements(pagedResult.getTotalElements());
		return pageDto;
	}

	@Override
	public MapSimpleDto archiveMap(MapDto mapDto, UUID id) {
		dz.eadn.sig.model.Map map = this.findById(id);
		MapSimpleDto mapSimpleDto = null;

		NotificationSimpleDto notification = null;

		if (map == null) {
			throw new EntityAlreadyExistsException(
					String.format("Map avec le nom <%s> est supprim� ", mapDto.getName()));

		} else {

			map.setPrivacy(mapDto.getPrivacy());

			// prepare and send the notification

			List<User> users = new ArrayList<>(map.getUsers());

			for (Group group : map.getGroups()) {
				users.addAll(group.getUsers());
			}
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			SystemNotification systemNotification = createSystemNotification(Transaction.UPDATE, null);

			if (mapDto.getPrivacy().equals(Privacy.ARCHIVED)) {
				String message = String.format(messages.getMessages().get("NM_MAP_ARCHIVE"), map.getName(),
						authentication.getName());
				notification = createNotification(NotificationLevel.INFO, Operation.ARCHIVAGE, message,
						systemNotification, users);

			} else if (mapDto.getPrivacy().equals(Privacy.PRIVATE)) {
				String message = String.format(messages.getMessages().get("NM_MAP_UNARCHIVE"), map.getName(),
						authentication.getName());
				notification = createNotification(NotificationLevel.INFO, Operation.DESARCHIVAGE, message,
						systemNotification, users);
			}

			mapSimpleDto = saveMap(modelMapper.map(map, MapDto.class), notification);

		}

		return mapSimpleDto;
	}

	@Override
	public PageDto<UserSimpleDto> getUsersSharingMap(Map map, Integer page, Integer limit, String sort, String dir) {
		PageDto<UserSimpleDto> pageDto = null;
		if (map != null) {
			pageDto = new PageDto<>();
			Sort sortDir = null;

			if (dir.equals("asc"))
				sortDir = Sort.by(sort).ascending();
			else
				sortDir = Sort.by(sort).descending();

			Pageable pageable = PageRequest.of(page, limit, sortDir);

			Page<User> usersPage = userRepository.findByMaps(map, pageable);
			List<UserSimpleDto> userSimpleDtos = cModelMapper.mapList(usersPage.getContent(), UserSimpleDto.class);
			pageDto.setContent(userSimpleDtos);
			pageDto.setTotalElements(usersPage.getTotalElements());
		}

		return pageDto;
	}

	@Override
	public PageDto<GroupSimpleDto> getGroupsSharingMap(Map map, Integer page, Integer limit, String sort, String dir) {

		PageDto<GroupSimpleDto> pageDto = null;
		if (map != null) {
			pageDto = new PageDto<>();
			Sort sortDir = null;

			if (dir.equals("asc"))
				sortDir = Sort.by(sort).ascending();
			else
				sortDir = Sort.by(sort).descending();

			Pageable pageable = PageRequest.of(page, limit, sortDir);

			Page<Group> groupsPage = groupRepository.findByMaps(map, pageable);
			List<GroupSimpleDto> groupSimpleDto = cModelMapper.mapList(groupsPage.getContent(), GroupSimpleDto.class);
			pageDto.setContent(groupSimpleDto);
			pageDto.setTotalElements(groupsPage.getTotalElements());
		}
		return pageDto;
	}

	@Override
	public MapSimpleWithOthersDto getMapSharedWithOthers(UUID id, String source, Integer page, Integer limit,
			String sort, String dir) {

		Map map = findById(id);
		MapSimpleWithOthersDto others = null;
		PageDto<UserSimpleDto> userPageDto = null;
		PageDto<GroupSimpleDto> groupPageDto = null;

		if (map != null) {

			if (source.equals("users"))
				userPageDto = getUsersSharingMap(map, page, limit, sort, dir);
			else if (source.equals("groups"))
				groupPageDto = getGroupsSharingMap(map, page, limit, sort, dir);
			else if (source.equals("all")) {
				userPageDto = getUsersSharingMap(map, page, limit, sort, dir);
				groupPageDto = getGroupsSharingMap(map, page, limit, sort, dir);
			}

			others = new MapSimpleWithOthersDto();

			others.setId(map.getId());
			others.setSlug(map.getSlug());
			others.setCreatedBy(map.getCreatedBy());
			others.setPrivacy(map.getPrivacy());
			others.setUsers(userPageDto);
			others.setGroups(groupPageDto);

		}
		return others;
	}

	public List<UUID> getLayersInMap(UUID mapId) {
		String query = "with styles_in_theme as \n" +
				"(select s.id from sig.style s where s.theme_style_id = (select t.id from sig.theme t inner join sig.map m  on m.id = t.theme_map where t.is_default = true and  m.id = ? \n"
				+
				")),\n" +
				" layer_map_style as (select s.layer_map_style from styles_in_theme st inner join sig.style s on s.id = st.id),\n"
				+
				"layer_ids as(select ml.layers_id from sig.map_layers ml where ml.map_layer_id in (select * from layer_map_style))\n"
				+
				"select l.id from sig.layer l inner join layer_ids ld on l.id = ld.layers_id";
		return jdbcTemplate.queryForList(query, UUID.class, mapId);
	}

	public List<HashMap<String, String>> getAllLayersMap(UUID mapId, boolean isPublic) {
		try {
			List<LayerProjection> layerProjectionList = layerRepository.findAllByIdInOrderByName(getLayersInMap(mapId));
			List<HashMap<String, String>> layers = new ArrayList<>();
			String query = buildLayersMapQuery();
			List<java.util.Map<String, Object>> layersMaps = jdbcTemplate.queryForList(query, mapId);
			List<LayerStyleSimpleDto> styles = layerStylesService.getDefaultStyleInLayerMap(layersMaps.stream()
					.map(lm -> UUID.fromString(String.valueOf(lm.get("map_layer_id")))).collect(Collectors.toList()));
			styles.forEach(s -> {
				HashMap<String, String> layer = new HashMap();
				layer.put("style", s.getName());
				java.util.Map<String, Object> mapLayer = layersMaps.stream()
						.filter(l -> l.get("map_layer_id").equals(s.getMapLayer())).collect(Collectors.toList()).get(0);
				if (mapLayer != null)
					layer.put("layer", layerProjectionList.stream()
							.filter(l -> l.getId().equals(UUID.fromString(mapLayer.get("layers_id").toString())))
							.collect(Collectors.toList()).get(0).getSlug());
				layer.put("order", mapLayer.get("layer_order").toString());
				layer.put("isVisible", mapLayer.get("is_visible").toString());
				layers.add(layer);
			});
			return layers;
		} catch (Exception e) {
			log.error("Unexpected error in getAllLayersMap", e);
			throw new RuntimeException("Une erreur inattendue s'est produite !", e);
		}
	}

	@Override
	public List<LayerSimpleWithFieldsDto> getLayersSimpleWithFields(UUID mapId, boolean authenticated) {
		try {
			List<LayerSimpleWithFieldsDto> withFieldsDtos = cModelMapper.mapList(
					layerRepository.findAllByIdIn(getLayersInMap(mapId)),
					LayerSimpleWithFieldsDto.class);
			String query = buildLayersMapQuery();
			List<java.util.Map<String, Object>> layersMaps = jdbcTemplate.queryForList(query, mapId);
			List<LayerStyleSimpleDto> styles = layerStylesService.getDefaultStyleInLayerMap(layersMaps.stream()
					.map(lm -> UUID.fromString(String.valueOf(lm.get("map_layer_id")))).collect(Collectors.toList()));
			if (styles != null && !styles.isEmpty()) {
				withFieldsDtos.forEach(l -> {
					java.util.Map<String, Object> mapLayer = layersMaps.stream()
							.filter(lm -> UUID.fromString(String.valueOf(lm.get("layers_id"))).equals(l.getId()))
							.collect(Collectors.toList()).get(0);
					if (mapLayer != null && !mapLayer.isEmpty())
						l.setStyle(styles.stream()
								.filter(s -> s.getMapLayer()
										.equals(UUID.fromString(String.valueOf(mapLayer.get("map_layer_id")))))
								.collect(Collectors.toList()).get(0));
					l.setOrder(Integer.parseInt(mapLayer.get("layer_order").toString()));
					l.setVisible(Boolean.parseBoolean(mapLayer.get("is_visible").toString()));

				});
			}
			if (authenticated == true) {

				for (LayerSimpleWithFieldsDto layer : withFieldsDtos) {
					List<FieldSimpleDto> fields = layer.getFields().stream().filter(field -> field.getVisible())
							.collect(Collectors.toList());
					layer.setFields(fields);
				}

			} else {

				for (LayerSimpleWithFieldsDto layer : withFieldsDtos) {
					List<FieldSimpleDto> fields = layer.getFields().stream().filter(field -> field.getVisible())
							.filter(field -> field.getPublique()).filter(field -> field.getPublique())
							.collect(Collectors.toList());
					layer.setFields(fields);
				}

			}

			return withFieldsDtos;
		} catch (Exception e) {
			log.error("Unexpected error in getLayersSimpleWithFields", e);
			throw new RuntimeException("Une erreur inattendue s'est produite !", e);
		}
	}

	@Override
	public List<LayerSimpleWithFieldsAndResourcesDto> getLayersSimpleWithFieldsAndResources(UUID mapId) {
		try {
			List<LayerSimpleWithFieldsAndResourcesDto> withFieldsAndResourcesDtos = cModelMapper.mapList(
					layerRepository.findAllByIdIn(getLayersInMap(mapId)),
					LayerSimpleWithFieldsAndResourcesDto.class);
			String query = buildLayersMapQuery();
			List<java.util.Map<String, Object>> layersMaps = jdbcTemplate.queryForList(query, mapId);
			List<LayerStyleSimpleDto> styles = layerStylesService.getDefaultStyleInLayerMap(layersMaps.stream()
					.map(lm -> UUID.fromString(String.valueOf(lm.get("map_layer_id")))).collect(Collectors.toList()));
			if (styles != null && !styles.isEmpty()) {
				withFieldsAndResourcesDtos.forEach(l -> {
					java.util.Map<String, Object> mapLayer = layersMaps.stream()
							.filter(lm -> UUID.fromString(String.valueOf(lm.get("layers_id"))).equals(l.getId()))
							.collect(Collectors.toList()).get(0);
					if (mapLayer != null && !mapLayer.isEmpty())
						l.setOrder(Integer.parseInt(mapLayer.get("layer_order").toString()));
					l.setVisible(Boolean.parseBoolean(mapLayer.get("is_visible").toString()));
					l.setMapLayerId(UUID.fromString(mapLayer.get("map_layer_id").toString()));
					List<LayerStyleSimpleDto> styleSimpleDtos = styles.stream().filter(
							s -> s.getMapLayer().equals(UUID.fromString(String.valueOf(mapLayer.get("map_layer_id")))))
							.collect(Collectors.toList());
					if (styleSimpleDtos != null && !styleSimpleDtos.isEmpty())
						l.setStyle(styleSimpleDtos.get(0));
				});
			}
			return withFieldsAndResourcesDtos;
		} catch (Exception e) {
			log.error("Unexpected error in getLayersSimpleWithFieldsAndResources", e);
			throw new RuntimeException("Une erreur inattendue s'est produite !", e);
		}
	}

	@Override
	public String buildLayersMapQuery() {
		return "with styles_in_theme as \n" +
				"(select s.id from sig.style s where s.theme_style_id = (select t.id from sig.theme t inner join sig.map m  on m.id = t.theme_map where t.is_default = true and  m.id = ? \n"
				+
				")),\n" +
				" layer_map_style as (select s.layer_map_style from styles_in_theme st inner join sig.style s on s.id = st.id)\n"
				+
				"select ml.map_layer_id, ml.layers_id, ml.layer_order, ml.is_visible from sig.map_layers ml where ml.map_layer_id in (select * from layer_map_style)";
	}

	@Override
	public Boolean checkIfThemeContainLayer(UUID themeId, UUID layerId) {
		String query = "with styles_in_theme as \n" +
				"(select s.id from sig.style s where s.theme_style_id = '" + themeId + "')," +
				" layer_map_style as (select s.layer_map_style from styles_in_theme st inner join sig.style s on s.id = st.id)\n"
				+
				" select 1 from sig.map_layers ml where ml.map_layer_id in (select * from layer_map_style) and ml.layers_id = '"
				+ layerId + "'";
		try {
			jdbcTemplate.queryForObject(query, String.class);
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	public MapSimpleDto saveMap(MapDto mapDto, NotificationSimpleDto notification) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		dz.eadn.sig.model.Map map = null;
		MapSimpleDto mapSimpleDto = null;

		if (mapDto != null) {

			dz.eadn.sig.model.Map existMap = mapRepository.findByNameIgnoreCase(mapDto.getName());

			if (existMap != null && !existMap.getId().equals(mapDto.getId())) {
				throw new EntityAlreadyExistsException(
						String.format("Map avec le nom <%s> est toujours exsite ", mapDto.getName()));
			}

			map = mapMapper.dtoToEntity(mapDto);

			if (mapDto.getId() == null) {
				// Init the default theme
				Theme defaultTheme = new Theme();
				defaultTheme.setIsDefault(true);
				defaultTheme.setMap(map);
				map.getThemes().add(defaultTheme);
				User owner = userService.findByUsername(authentication.getName());

				if (owner != null && mapDto.getUserDtos() != null
						&& mapDto.getUserDtos().stream().noneMatch(u -> u.getEmail().equals(owner.getEmail())))
					map.getUsers().add(owner);
			}

			map.setSlug(Utils.toSlug(map.getName()));

			map = mapRepository.save(map);

			mapSimpleDto = modelMapper.map(map, MapSimpleDto.class);

			// prepare and send the notification

			notification.getSystemNotification().setObject(mapSimpleDto);
			notificationMessageService.sendNotificationMessage(notification);

		}

		return mapSimpleDto;

	}

	@Override
	public MapSimpleDto createMap(MapDto mapDto) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String message = String.format(messages.getMessages().get("NM_MAP_CREATE"), mapDto.getName(),
				authentication.getName());
		SystemNotification systemNotification = createSystemNotification(Transaction.ADD, null);

		NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.CREATION, message,
				systemNotification, new ArrayList<>());
		return saveMap(mapDto, notification);
	}

	@Override
	public MapSimpleDto updateMap(MapDto mapDto) {

		List<User> users = new ArrayList<>();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Map map = findById(mapDto.getId());

		if (map != null) {

			users.addAll(map.getUsers());

			for (Group group : map.getGroups()) {
				users.addAll(group.getUsers());
			}

			map.setName(mapDto.getName());

		}

		String message = String.format(messages.getMessages().get("NM_MAP_UPDATE"), mapDto.getName(),
				authentication.getName());
		SystemNotification systemNotification = createSystemNotification(Transaction.UPDATE, null);

		NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.MODIFICATION, message,
				systemNotification, users);

		return saveMap(modelMapper.map(map, MapDto.class), notification);
	}

	@Override
	public MapSimpleDto cloneMap(String mapSlug, CloneMapDto cloneMapDto) {
		Map map = mapRepository.findBySlug(mapSlug);
		MapDto clonedMap = mapMapper.entityToDto(map);
		clonedMap.setId(null);
		clonedMap.setName(cloneMapDto.getOutputName());
		clonedMap.setPrivacy(cloneMapDto.getPrivacy());
		if (cloneMapDto.getCloneUsers()) {
			List<UserDto> userDtoList = cModelMapper.mapList(map.getUsers(), UserDto.class);
			userDtoList.forEach(u -> u.setPassword(null));
			clonedMap.setUserDtos(userDtoList);
		} else {
			clonedMap.setUserDtos(null);
		}
		if (!cloneMapDto.getCloneGroups()) {
			clonedMap.setGroupDtos(null);
		}
		MapSimpleDto savedMap = createMap(clonedMap);
		List<MapLayerDto> mapLayerDtoList = new ArrayList<>();
		map.getLayers().forEach(l -> {
			MapLayerDto mapLayerDto = new MapLayerDto();
			mapLayerDto.setLayer(modelMapper.map(l.getLayer(), LayerSimpleDto.class));
			mapLayerDto.setMap(savedMap);
			mapLayerDtoList.add(mapLayerDto);
		});
		mapLayerService.attachLayersToMap(mapLayerDtoList, false);
		return savedMap;
	}

	public NotificationSimpleDto createNotification(NotificationLevel level, Operation operation, String message,
			SystemNotification systemNotification, List<User> users) {

		NotificationSimpleDto notification = new NotificationSimpleDto();

		notification.setObject(NotificationObject.Carte);
		notification.setLevel(level);
		notification.setOperation(operation);
		notification.setDestination("map");
		notification.setMessage(message);
		notification.setSystemNotification(systemNotification);
		notification.setUsers(users);

		return notification;
	}

	public SystemNotification createSystemNotification(Transaction transaction, Object object) {
		SystemNotification systemNotification = new SystemNotification();

		systemNotification.setType("maps");
		systemNotification.setTransaction(transaction);
		systemNotification.setObject(object);

		return systemNotification;
	}

}
