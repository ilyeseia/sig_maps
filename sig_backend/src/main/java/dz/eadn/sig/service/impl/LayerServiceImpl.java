package dz.eadn.sig.service.impl;

import java.io.IOException;
import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

import dz.eadn.sig.dto.*;
import dz.eadn.sig.mapper.*;
import dz.eadn.sig.model.*;
import dz.eadn.sig.repository.*;
import dz.eadn.sig.service.*;
import dz.eadn.sig.util.*;
import org.geotools.geometry.jts.GeometryBuilder;
import org.locationtech.jts.geom.Geometry;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import dz.eadn.sig.constants.Constants;
import dz.eadn.sig.exceptions.AccessNotPermittedException;
import dz.eadn.sig.exceptions.EntityAlreadyExistsException;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.exceptions.GlobalException;
import dz.eadn.sig.service.common.impl.CommonServiceImpl;
import feign.FeignException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour & A.LAMOUR & C.LOKBANI
 */
@Service
@Slf4j
public class LayerServiceImpl extends CommonServiceImpl<Layer, LayerDto> implements LayerService {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private LayerRepository layerRepository;

	@Autowired
	private GeoserverService geoserverService;

	@Autowired
	private GeoToolsService geoToolsService;

	@Autowired
	private EntityElementRepository entityElementRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	@Qualifier("layerUserMapper")
	private LayerUserMapper luMapper;

	@Autowired
	private LayerMapper layerMapper;

	@Autowired
	private EntityElementService entityElementService;

	@Autowired
	private FieldMapper fieldMapper;

	@Autowired
	private FieldService fieldService;

	@Autowired
	SettingsService settingsService;

	@Autowired
	UploadFileService uploadFileService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private CommonModelMapper<?, ?> cModelMapper;

	@Autowired
	private NotificationMessagesDto messages;

	@Autowired
	private NotificationMessageService notificationMessageService;

	@Autowired
	private MapService mapService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private GroupMapper groupMapper;

	@Autowired
	private GroupService groupService;

	@Autowired
	private LayerStylesService layerStylesService;

	@Autowired
	private UserLoggedActionsService userLoggedActionsService;

	@Autowired
	private FilterRepository filterRepository;

	public LayerServiceImpl() {
		super(Layer.class);
	}

	boolean forceDelete = false;

	@Override
	public void setForceDelete(boolean value) {
		this.forceDelete = value;
	}

	@Override
	public Layer findBySlug(String slug) {
		return layerRepository.findBySlug(slug);
	}

	@Override
	public Layer findById(UUID id) {
		return super.findById(id);
	}

	@Transactional
	@Override
	public void delete(UUID id) {
		Layer layer = findById(id);
		if (!this.forceDelete) {
			CheckIfUserHasPrivilegeOnLayer(layer.getSlug(), null, null, "write");
		}
		if (layer == null) {
			throw new EntityNotFoundException("Impossible de supprimer l'entite " + domainClass.getSimpleName());
		}
		if (layer.getType().equals(LayerType.RASTER)) {
			try {
				geoserverService.deleteCoverageStore(layer.getSlug());
			} catch (FeignException e) {
				// Delete if we are in the creation mode or the layer does not exists (update
				// mode)
				if (!this.forceDelete && e.status() != 404) {
					throw new GlobalException(
							"L'op�ration de suppression de couche " + layer.getName() + " a �chou�");
				}
			}
		} else {
			try {
				geoserverService.deleteLayer(layer.getSlug());
			} catch (FeignException e) {
				if (!this.forceDelete && e.status() != 404) {
					throw new GlobalException(
							"L'op�ration de suppression de couche " + layer.getName() + " a �chou�");
				}
			}

			// Delete all layer styles in the geoserver
			layerStylesService.deleteStylesByLayer(layer);

			String layerViewName = layer.getSlug() + "_view";
			/*
			 * for (MapLayer ml : layer.getMapLayers()) { ml.getLayer().remove(layer);
			 * mapRepository.save(map); }
			 */

			// Delete all layer filters
			layer.getUserLayerFilters().forEach(u -> {
				filterRepository.delete(u.getFilter());
			});
			layerRepository.delete(layer);
			try {
				uploadFileService.deleteFolder("/" + Constants.FOLDER_IMAGES_LAYERS, layer.getId().toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				jdbcTemplate.execute("DROP VIEW IF EXISTS \"" + layerViewName + "\"");
			} catch (Exception e) {
				e.printStackTrace();
			}

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			String message = String.format(messages.getMessages().get("NM_LAYER_DELETE"), layer.getName(),
					authentication.getName());

			SystemNotification systemNotification = createSystemNotification(Transaction.DELETE,
					modelMapper.map(layer, LayerSimpleDto.class));

			NotificationSimpleDto notification = createNotification(NotificationLevel.SEVERE, Operation.SUPPRISSION,
					message, systemNotification, new ArrayList<>());

			notificationMessageService.sendNotificationMessage(notification);
		}
	}

	@Override
	public long count() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userName = authentication.getName();

		TypedQuery<Long> query = entityManager.createQuery(
				"SELECT count(layer) from Layer layer WHERE (select user from User user where user.username = :username) MEMBER OF layer.users",
				Long.class);

		query.setParameter("username", userName);
		return query.getSingleResult();
	}

	// used to create layer view when the layer is empty and have no entity elements
	private EntityElement createFakeEntityElement(Layer layer) {
		EntityElement ee = layer.getViewElement();
		if (ee == null) {
			ee = new EntityElement();
			ee.setLayer(layer);

			ee.setCreateDate(new Date());
			ee.setCreatedBy("admin");
			ee.setLastModifiedDate(new Date());
			ee.setModifiedBy("admin");

			ee.setDeleted(true);
			layer.getEntityElements().add(ee);
		}
		GeometryBuilder builder = new GeometryBuilder();
		Geometry geom = null;
		switch (layer.getTopo()) {
			case "Point":
				geom = builder.point();
				break;
			case "Polygon":
				geom = builder.polygon();
				break;
			case "LineString":
				geom = builder.lineString();
				break;
			case "MultiPolygon":
				geom = builder.multiPolygon(null);
		}

		geom.setSRID(4326);
		ee.setGeom(geom);

		Map<String, String> props = new HashMap<String, String>();
		for (Field field : layer.getFields()) {
			if (field.getDeleted().equals(false))
				props.put(field.getSlug(), "");
		}
		ee.setProperties(props);
		return ee;
	}

	@Override
	public void createSqlView(Layer layer) {
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("create_jsonb_flat_view");
		query.registerStoredProcedureParameter("table_name", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("regular_columns", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("json_column", String.class, ParameterMode.IN);

		query.setParameter("table_name", Utils.toSlug(layer.getName()));
		query.setParameter("regular_columns",
				"id,create_date,created_by,deleted,last_modified_date,modified_by,geom,layer_entity_element");
		query.setParameter("json_column", "properties");

		EntityElement ee = this.createFakeEntityElement(layer);
		EntityElement viewElement = entityElementRepository.save(ee);
		layer.setViewElement(viewElement);
		layerRepository.save(layer);
		query.execute();
	}

	@Override
	public LayerDto save(LayerDto layerDto) {
		if (layerDto != null) {
			if (layerDto.getName().contains("-")) {
				throw new GlobalException("Le nom de la couche ne doit pas contenir ce caractère (-)");
			}
			if (layerDto.getId() != null) {
				return updateLayer(layerDto);
			} else {
				if (layerRepository.findByNameIgnoreCase(layerDto.getName()) != null) {
					throw new EntityAlreadyExistsException(
							String.format("layer avec le nom <%s> est toujours exsite ", layerDto.getName()));
				} else {
					return createLayer(layerDto);
				}
			}
		}
		return null;
	}

	@Transactional
	@Override
	public LayerDto updateLayer(LayerDto layerDto) {
		CheckIfUserHasPrivilegeOnLayer(layerDto.getSlug(), null, null, "write");
		Layer oldLayer = findById(layerDto.getId());
		String oldLayerSlug = oldLayer.getSlug();
		List<Field> oldFields = new ArrayList<>();
		oldLayer.getFields().forEach(f -> oldFields.add(f));
		Layer layer = layerMapper.dtoToEntity(layerDto);
		String oldFeatureType = null;
		boolean viewNeedUpdate = false;
		// Check if there are duplication in incoming fields before updating
		if (layer.getFields() != null) {
			// Update the view if layer fields number changes
			if (layer.getFields().size() != oldFields.size()) {
				viewNeedUpdate = true;
			}
			String oldSlug = "";
			for (Field field : layer.getFields()) {
				if (layer.getId() != null && field.getId() == null
						&& fieldService.findBySlugAndLayer(Utils.toSlug(field.getName()), layer)) {
					throw new EntityAlreadyExistsException(
							String.format("Ce champ avec le nom <%s> est toujours exsite ", field.getName()));
				}
				if (field.getId() != null) {
					oldSlug = field.getSlug();
					// if the field already exists and dirty
					if (!oldSlug.equals(Utils.toSlug(field.getName()))) {
						// get number of fields that will have same slug
						int duplications = layer.getFields().stream()
								.filter(f -> f.getId() != null && f.getId() != field.getId()
										&& Utils.toSlug(f.getName()).equals(Utils.toSlug(field.getName())))
								.collect(Collectors.toList()).size();
						viewNeedUpdate = true;
						// if we'll have more than one; break
						if (duplications > 1) {
							throw new EntityAlreadyExistsException(
									String.format("Ce champ avec le nom <%s> est toujours existe ", field.getName()));
						}
					}
				} else {
					viewNeedUpdate = true;
				}
			}
		}
		layer.setSlug(Utils.toSlug(layer.getName()));

		// Update layer in geoserver
		if (viewNeedUpdate || !(oldLayerSlug).equals(Utils.toSlug(layer.getName()))) {
			try {
				geoserverService.updateLayer(oldLayerSlug, new SLDGeneratorImpl().createFeatureType(layerDto));
			} catch (FeignException e) {
				// If the layer does not exits: ROLLBACK
				if (e.status() == 404) {
					jdbcTemplate.execute("DROP VIEW IF EXISTS " + oldLayerSlug + "_view");
					this.createSqlView(layer);
					layerDto.setSlug(Utils.toSlug(layerDto.getName()));
					geoserverService.addLayer(new SLDGeneratorImpl().createFeatureType(layerDto));
					try {
						geoserverService.deleteLayer(oldLayerSlug);
					} catch (FeignException e1) {
						log.error("Error deleting old layer during update rollback", e1);
					}
					viewNeedUpdate = false;
				} else {
					throw new GlobalException("l'opération de la modification a échoué !");
				}
			}
		}

		List<Field> newFields = null;
		// Remove deleted property from JSONB
		if (layerDto.getFieldDtos() != null) {
			newFields = fieldMapper.dtosToEntitys(layerDto.getFieldDtos());
			String qry1 = "UPDATE sig.entity_element SET properties = properties";
			String qry2 = "";
			if (oldFields != null) {
				for (Field field : oldFields) {
					if (!newFields.contains(field)) {
						qry2 += " - '" + field.getSlug() + "'";
					}
				}
			}
			if (qry2 != "") {
				String query = qry1 + qry2 + " WHERE layer_entity_element ='" + oldLayer.getId() + "'";
				jdbcTemplate.update(query);
			}
		}

		if (layer.getFields() != null) {
			String qry1 = "UPDATE sig.entity_element SET properties = properties";
			String oldSlug = "";
			for (Field field : layer.getFields()) {
				if (field.getId() != null) {
					oldSlug = field.getSlug();
					// if the field already exists and dirty
					if (!oldSlug.equals(Utils.toSlug(field.getName()))) {
						field.setSlug(Utils.toSlug(field.getId().toString()));
						String query = qry1 + "- '" + oldSlug.replaceAll("'", "''") + "' || jsonb_build_object('"
								+ field.getSlug().replaceAll("'", "''")
								+ "', properties->'" + oldSlug.replaceAll("'", "''") + "') where properties ? '"
								+ oldSlug.replaceAll("'", "''")
								+ "' and layer_entity_element = " + "'" + layer.getId() + "'";
						try {
							jdbcTemplate.update(query);

						} catch (Exception e) {
							log.error("Error updating entity element properties for field slug update", e);
						}
					}
				} else {
					field.setSlug(Utils.toSlug(field.getName()));
				}
			}
		}

		layerRepository.save(layer);
		// update fields slug in case of switch operation
		layer.getFields().stream().filter(f -> !f.getSlug().equals(Utils.toSlug(f.getName()))).forEach(f -> {
			String oldSlug = f.getSlug();
			f.setSlug(Utils.toSlug(f.getName()));
			String query = "UPDATE sig.entity_element SET properties = properties - '" + oldSlug.replaceAll("'", "''")
					+ "' || jsonb_build_object('" + f.getSlug().replaceAll("'", "''") + "', properties->'"
					+ oldSlug.replaceAll("'", "''")
					+ "') where properties ? '" + oldSlug.replaceAll("'", "''") + "' and layer_entity_element = " + "'"
					+ layer.getId() + "'";
			try {
				jdbcTemplate.update(query);
			} catch (Exception e) {
				log.error("Error updating field slugs switch", e);
			}
		});

		Layer savedLayer = layerRepository.save(layer);
		LayerDto saveLayerDto = luMapper.entityToDto(savedLayer);

		// update the view
		if (viewNeedUpdate || !(oldLayerSlug).equals(Utils.toSlug(layer.getName()))) {
			jdbcTemplate.execute("DROP VIEW IF EXISTS " + oldLayerSlug + "_view");
			createSqlView(savedLayer);
		}

		// prepare notifications

		List<User> users = new ArrayList<>();

		if (layer != null) {

			users.addAll(layer.getUsers());

			for (Group group : layer.getGroups()) {
				users.addAll(group.getUsers());
			}

		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String message = String.format(messages.getMessages().get("NM_LAYER_UPDATE"), saveLayerDto.getName(),
				authentication.getName());

		SystemNotification systemNotification = createSystemNotification(Transaction.UPDATE, saveLayerDto);

		NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.MODIFICATION, message,
				systemNotification, users);

		notificationMessageService.sendNotificationMessage(notification);

		return saveLayerDto;
	}

	@Transactional
	@Override
	public LayerDto createLayer(LayerDto layerDto) {
		Layer layer = layerMapper.dtoToEntity(layerDto);
		layer.setSlug(Utils.toSlug(layer.getName()));
		List<String> addedFieldsSlug = new ArrayList<>();
		if (layer.getFields() != null) {
			for (Field field : layer.getFields()) {
				field.setSlug(Utils.toSlug(field.getName()));
				field.setLayer(layer);
				if (addedFieldsSlug.stream().filter(f -> f.equals(field.getSlug())).collect(Collectors.toList())
						.size() > 0) {
					throw new GlobalException("Le champ " + field.getName() + " est ajouté plusieurs fois");
				} else {
					addedFieldsSlug.add(field.getSlug());
				}
			}
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		User owner = userService.findByUsername(username);
		if (owner != null && layerDto.getUserDtos() != null
				&& layerDto.getUserDtos().stream().noneMatch(u -> u.getEmail().equals(owner.getEmail()))) {
			layer.getUsers().add(owner);
		}
		Layer savedLayer = layerRepository.save(layer);
		LayerDto saveLayerDto = luMapper.entityToDto(savedLayer);
		try {
			createSqlView(savedLayer);
			geoserverService.addLayer(new SLDGeneratorImpl().createFeatureType(layerDto));
		} catch (Exception e) {
			this.forceDelete = true;
			delete(savedLayer.getId());
			this.forceDelete = false;
			throw new GlobalException("l'opération d'ajout de couche a échoué !");
		}

		// set the parent of field
		for (FieldDto fieldDto : layerDto.getFieldDtos()) {
			if (fieldDto.getParent() != null) {
				Optional<Field> optional = fieldService.findByNameAndLayer(fieldDto.getParent(), layer);
				if (optional.isPresent()) {
					layer.getFields().stream().filter(field -> field.getSlug() == Utils.toSlug(fieldDto.getName()))
							.map(field -> {
								field.setParent(optional.get().getId());
								return field;
							}).collect(Collectors.toList());
				}
			}
		}

		layerRepository.save(layer);

		// prepare notifications

		String message = String.format(messages.getMessages().get("NM_LAYER_CREATE"), savedLayer.getName(),
				authentication.getName());

		SystemNotification systemNotification = createSystemNotification(Transaction.ADD, saveLayerDto);

		NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.CREATION, message,
				systemNotification, new ArrayList<>());

		notificationMessageService.sendNotificationMessage(notification);

		return saveLayerDto;
	}

	@Override
	public List<Layer> findAll() {
		return layerRepository.findAll();
	}

	@Override
	public List<LayerProjection> findAllBYSlug() {
		return layerRepository.findAllLayer();
	}

	@Override
	public List<Layer> findAll(Integer page, Integer limit, String sort, String dir) {
		return super.findAll(page, limit, sort, dir);
	}

	@Override
	public void shareLayer(UUID id, ShareLayerWithOthers sharedLayer) {

		Layer layer = this.findById(id);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		List<User> deletedUsers = new ArrayList<>();
		List<User> addedUsers = new ArrayList<>();

		if (layer != null) {

			// set new users
			if (sharedLayer.getUsers() != null) {

				sharedLayer.getUsers().forEach(user -> {
					if (user.getIsNew() != null && user.getIsNew()) {

						// fill the list of added users
						addedUsers.add(modelMapper.map(user, User.class));

						layer.getUsers().add(userMapper.dtoToEntity(user));
					} else if (user.getToDelete() != null && user.getToDelete()) {

						// fill the list of deleted users
						deletedUsers.add(modelMapper.map(user, User.class));

						layer.getUsers().remove(userMapper.dtoToEntity(user));
					}
				});
			}

			if (sharedLayer.getGroups() != null) {

				// set the new users from layer groups
				for (GroupDto g : sharedLayer.getGroups()) {

					if (g.getIsNew() != null && g.getIsNew()) {

						// fill the list of added users
						Group group = groupService.findById(g.getId());
						if (group != null) {
							addedUsers.addAll(group.getUsers());
						}

						layer.getGroups().add(groupMapper.dtoToEntity(g));
					} else if (g.getToDelete() != null && g.getToDelete()) {

						// fill the list of deleted users

						Group group = groupService.findById(g.getId());
						if (group != null) {
							deletedUsers.addAll(group.getUsers());
						}

						layer.getGroups().remove(groupMapper.dtoToEntity(g));
					}
				}
			}

			Layer savedLayer = layerRepository.save(layer);
			LayerSimpleDto layerSimpleDto = modelMapper.map(savedLayer, LayerSimpleDto.class);

			// Audit share layer action
			List<java.util.Map<String, String>> properties = new ArrayList<>();
			java.util.Map<String, String> property = new LinkedHashMap<>();
			property.put("attribute", "users");
			property.put("addedValues",
					sharedLayer.getUsers().stream().filter(u -> u.getIsNew() != null && u.getIsNew())
							.map(WITHUUID::getId).collect(Collectors.toList()).toString());
			property.put("deletedValues",
					sharedLayer.getUsers().stream().filter(u -> u.getToDelete() != null && u.getToDelete())
							.map(WITHUUID::getId).collect(Collectors.toList()).toString());
			java.util.Map<String, String> property2 = new LinkedHashMap<>();
			property2.put("attribute", "groups");
			property2.put("addedValues",
					sharedLayer.getGroups().stream().filter(g -> g.getIsNew() != null && g.getIsNew())
							.map(WITHUUID::getId).collect(Collectors.toList()).toString());
			property2.put("deletedValues",
					sharedLayer.getGroups().stream().filter(g -> g.getToDelete() != null && g.getToDelete())
							.map(WITHUUID::getId).collect(Collectors.toList()).toString());
			properties.add(property);
			properties.add(property2);
			userLoggedActionsService.createAudit(properties, id, "Layer");

			// prepare the notification for deleted users
			if (deletedUsers != null && deletedUsers.size() > 0) {

				SystemNotification systemNotification = createSystemNotification(Transaction.DELETE, layerSimpleDto);

				String message = String.format(messages.getMessages().get("NM_LAYER_UNSHARE"), layerSimpleDto.getName(),
						authentication.getName());

				NotificationSimpleDto notifDeleted = createNotification(NotificationLevel.SEVERE, Operation.DEPARTAGE,
						message, systemNotification, deletedUsers);

				notificationMessageService.sendNotificationMessage(notifDeleted);

			}

			// prepare the notification for new users
			if (addedUsers != null && addedUsers.size() > 0) {

				SystemNotification systemNotification = createSystemNotification(Transaction.ADD, layerSimpleDto);

				String message = String.format(messages.getMessages().get("NM_LAYER_SHARE"), layerSimpleDto.getName(),
						authentication.getName());

				NotificationSimpleDto notifAdded = createNotification(NotificationLevel.INFO, Operation.PARTAGE,
						message, systemNotification, addedUsers);

				notificationMessageService.sendNotificationMessage(notifAdded);
			}

		}

	}

	@Override
	public PageDto<LayerDto> findAllByPage(Integer page, Integer limit, String sort, String dir) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Layer> cq = cb.createQuery(Layer.class);
		Root<Layer> rootLayer = cq.from(Layer.class);

		Join<Layer, User> LayerjoinUser = rootLayer.join("users", JoinType.LEFT);
		Join<Layer, Group> LayerjoinGroup = rootLayer.join("groups", JoinType.LEFT);

		TypedQuery<Layer> typedQuery = null;

		PageDto<LayerDto> pageDto = new PageDto<>();
		Pageable pageable = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
			String username = authentication.getName();

			if (userService.isAdministrateur(username)) {
				if (dir.equals("asc"))
					pageable = PageRequest.of(page, limit, Sort.by(sort).ascending());
				else
					pageable = PageRequest.of(page, limit, Sort.by(sort).descending());

				Page<Layer> layerPage = layerRepository.findAll(pageable);

				pageDto.setContent(layerMapper.entitysToDtos(layerPage.getContent()));
				pageDto.setTotalElements(layerPage.getTotalElements());

			} else {
				User user = userService.findByUsername(username);

				List<UUID> groupIds = new ArrayList<>();

				for (Group group : user.getGroups()) {
					groupIds.add(group.getId());
				}

				typedQuery = entityManager.createQuery(cq.select(rootLayer).distinct(true).where(
						cb.or(cb.equal(LayerjoinUser.get("id"), user.getId()), LayerjoinGroup.get("id").in(groupIds))));

				int total = typedQuery.getResultList().size();

				if (limit != -1)
					typedQuery.setFirstResult(page * limit).setMaxResults(limit);

				List<LayerDto> layerDtos = layerMapper.entitysToDtos(typedQuery.getResultList());

				pageDto.setContent(layerDtos);
				pageDto.setTotalElements(total);

			}

		}

		return pageDto;

	}

	@Override
	public PageDto<LayerDto> findAllByFilter(CommonFilter filter, Integer page, Integer limit, String sort,
			String dir) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Layer> cq = cb.createQuery(Layer.class);
		Root<Layer> root = cq.from(Layer.class);

		Join<Layer, User> LayerjoinUser = root.join("users", JoinType.LEFT);
		Join<Layer, Group> LayerjoinGroup = root.join("groups", JoinType.LEFT);

		List<Predicate> predicates = new ArrayList<>();
		PageDto<LayerDto> pageDto = new PageDto<>();

		Predicate deletePre, commonsPre, ownerPre, finalPre = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		if (!userService.isAdministrateur(username)) {

			User user = userService.findByUsername(username);

			List<UUID> groupIds = new ArrayList<>();

			for (Group group : user.getGroups()) {
				groupIds.add(group.getId());
			}

			ownerPre = cb.or(cb.equal(LayerjoinUser.get("id"), user.getId()), LayerjoinGroup.get("id").in(groupIds));
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

		TypedQuery<Layer> typedQuery = entityManager.createQuery(cq.select(root).distinct(true).where(finalPre));

		int total = typedQuery.getResultList().size();

		if (limit != -1)
			typedQuery.setFirstResult(page * limit).setMaxResults(limit);

		List<Layer> layers = typedQuery.getResultList();

		pageDto.setContent(mapper.entitysToDtos(layers));
		pageDto.setTotalElements(total);

		return pageDto;

	}

	@Override
	public LayerSimpleWithFieldsDto getLayerWithFields(UUID id) {

		LayerSimpleWithFieldsDto simpleWithFieldsDto = null;
		Layer layer = findById(id);
		CheckIfUserHasPrivilegeOnLayer(layer.getSlug(), null, null, "write");
		if (layer != null) {
			simpleWithFieldsDto = modelMapper.map(layer, LayerSimpleWithFieldsDto.class);
		}

		return simpleWithFieldsDto;
	}

	@Override
	public PageDto<LayerSimpleDto> findAllLayersByPage(Integer page, Integer limit, String sort, String dir) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Layer> cq = cb.createQuery(Layer.class);
		Root<Layer> rootLayer = cq.from(Layer.class);

		Join<Layer, User> LayerjoinUser = rootLayer.join("users", JoinType.LEFT);
		Join<Layer, Group> LayerjoinGroup = rootLayer.join("groups", JoinType.LEFT);

		TypedQuery<Layer> typedQuery = null;

		PageDto<LayerSimpleDto> pageDto = new PageDto<>();

		Pageable pageable = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
			String username = authentication.getName();

			if (userService.isAdministrateur(username)) {
				if (dir.equals("asc"))
					pageable = PageRequest.of(page, limit, Sort.by(sort).ascending());
				else
					pageable = PageRequest.of(page, limit, Sort.by(sort).descending());

				Page<Layer> layerPage = layerRepository.findAll(pageable);

				List<LayerSimpleDto> layerTableDtos = cModelMapper.mapList(layerPage.getContent(),
						LayerSimpleDto.class);

				pageDto.setContent(layerTableDtos);
				pageDto.setTotalElements(layerPage.getTotalElements());

			} else {
				User user = userService.findByUsername(username);

				List<UUID> groupIds = new ArrayList<>();

				for (Group group : user.getGroups()) {
					groupIds.add(group.getId());
				}

				typedQuery = entityManager.createQuery(cq.select(rootLayer).distinct(true).where(
						cb.or(cb.equal(LayerjoinUser.get("id"), user.getId()), LayerjoinGroup.get("id").in(groupIds))));

				int total = typedQuery.getResultList().size();

				if (limit != -1)
					typedQuery.setFirstResult(page * limit).setMaxResults(limit);

				List<Layer> layers = typedQuery.getResultList();

				List<LayerSimpleDto> layerTableDtos = cModelMapper.mapList(layers, LayerSimpleDto.class);

				pageDto.setContent(layerTableDtos);
				pageDto.setTotalElements(total);

			}

		}

		return pageDto;
	}

	@Override
	public PageDto<LayerSimpleDto> findAllLayersByFilter(CommonFilter filter, Integer page, Integer limit, String sort,
			String dir) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Layer> cq = cb.createQuery(Layer.class);
		Root<Layer> root = cq.from(Layer.class);

		Join<Layer, User> LayerjoinUser = root.join("users", JoinType.LEFT);
		Join<Layer, Group> LayerjoinGroup = root.join("groups", JoinType.LEFT);

		List<Predicate> predicates = new ArrayList<>();
		PageDto<LayerSimpleDto> pageDto = new PageDto<>();

		Predicate deletePre, commonsPre, ownerPre, finalPre = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		if (!userService.isAdministrateur(username)) {

			User user = userService.findByUsername(username);

			List<UUID> groupIds = new ArrayList<>();

			for (Group group : user.getGroups()) {
				groupIds.add(group.getId());
			}

			ownerPre = cb.or(cb.equal(LayerjoinUser.get("id"), user.getId()), LayerjoinGroup.get("id").in(groupIds));
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

		TypedQuery<Layer> typedQuery = entityManager.createQuery(cq.select(root).distinct(true).where(finalPre));

		int total = typedQuery.getResultList().size();

		if (limit != -1)
			typedQuery.setFirstResult(page * limit).setMaxResults(limit);

		List<Layer> layers = typedQuery.getResultList();

		pageDto.setContent(cModelMapper.mapList(layers, LayerSimpleDto.class));
		pageDto.setTotalElements(total);

		return pageDto;
	}

	@Override
	public PageDto<LayerSimpleDto> findAllLayersByFilterInMap(CommonFilter filter, UUID mapId, Integer page,
			Integer limit, String sort, String dir) {
		checkIfMapSharedWithUser(mapId);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Layer> cq = cb.createQuery(Layer.class);
		Root<Layer> root = cq.from(Layer.class);

		Join<Layer, User> LayerjoinUser = root.join("users", JoinType.LEFT);
		Join<Layer, Group> LayerjoinGroup = root.join("groups", JoinType.LEFT);

		List<Predicate> predicates = new ArrayList<>();
		PageDto<LayerSimpleDto> pageDto = new PageDto<>();

		Predicate deletePre, commonsPre, ownerPre, finalPre = null;
		Predicate filterPre = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		if (!userService.isAdministrateur(username)) {

			User user = userService.findByUsername(username);

			List<UUID> groupIds = new ArrayList<>();

			for (Group group : user.getGroups()) {
				groupIds.add(group.getId());
			}

			ownerPre = cb.or(cb.equal(LayerjoinUser.get("id"), user.getId()), LayerjoinGroup.get("id").in(groupIds));
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

		dz.eadn.sig.model.Map map = mapService.findById(mapId);

		List<UUID> layerIds = null;

		if (map != null) {
			String query = mapService.buildLayersMapQuery();
			List<Map<String, Object>> mapLayers = jdbcTemplate.queryForList(query, map.getId());
			layerIds = mapLayers.stream().map(ml -> UUID.fromString(String.valueOf(ml.get("layers_id"))))
					.collect(Collectors.toList());
			if (layerIds.size() > 0)
				filterPre = cb.not(root.get("id").in(layerIds));
		}

		if (filter.getCondition().equals("or"))
			commonsPre = cb.or(predicates.toArray(new Predicate[] {}));
		else
			commonsPre = cb.and(predicates.toArray(new Predicate[] {}));

		if (filterPre != null)

			finalPre = cb.and(commonsPre, deletePre, filterPre);
		else
			finalPre = cb.and(commonsPre, deletePre);

		TypedQuery<Layer> typedQuery = entityManager.createQuery(cq.select(root).distinct(true).where(finalPre));

		int total = typedQuery.getResultList().size();

		if (limit != -1)
			typedQuery.setFirstResult(page * limit).setMaxResults(limit);

		List<Layer> layers = typedQuery.getResultList();

		pageDto.setContent(cModelMapper.mapList(layers, LayerSimpleDto.class));
		pageDto.setTotalElements(total);

		return pageDto;
	}

	@Override
	public PageDto<LayerSimpleWithFieldsDto> findAllLayersSharedInMap(UUID mapId, Integer page, Integer limit,
			String sort, String dir) {
		checkIfMapSharedWithUser(mapId);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Layer> cq = cb.createQuery(Layer.class);
		Root<Layer> rootLayer = cq.from(Layer.class);

		Join<Layer, User> LayerjoinUser = rootLayer.join("users", JoinType.LEFT);
		Join<Layer, Group> LayerjoinGroup = rootLayer.join("groups", JoinType.LEFT);
		Join<Layer, dz.eadn.sig.model.Map> LayerjoinMap = rootLayer.join("maps", JoinType.LEFT);

		TypedQuery<Layer> typedQuery = null;

		PageDto<LayerSimpleWithFieldsDto> pageDto = new PageDto<>();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		User user = userService.findByUsername(username);

		List<UUID> groupIds = new ArrayList<>();

		for (Group group : user.getGroups()) {
			groupIds.add(group.getId());
		}

		if (dir == "asc")
			cq.orderBy(cb.asc(rootLayer.get(sort)));
		else
			cq.orderBy(cb.desc(rootLayer.get(sort)));

		dz.eadn.sig.model.Map map = mapService.findById(mapId);

		if (map != null) {
			typedQuery = entityManager
					.createQuery(cq.select(rootLayer).distinct(true)
							.where(cb.and(
									cb.or(cb.equal(LayerjoinUser.get("id"), user.getId()),
											LayerjoinGroup.get("id").in(groupIds)),
									cb.equal(LayerjoinMap.get("map"), map))));

			int total = typedQuery.getResultList().size();

			if (limit != -1)
				typedQuery.setFirstResult(page * limit).setMaxResults(limit);

			List<LayerSimpleWithFieldsDto> layerSimpleDtos = cModelMapper.mapList(typedQuery.getResultList(),
					LayerSimpleWithFieldsDto.class);

			pageDto.setContent(layerSimpleDtos);
			pageDto.setTotalElements(total);
		}
		return pageDto;
	}

	@Override
	public PageDto<LayerSimpleWithFieldsDto> findAllSharedLayers(UUID mapId, Integer page, Integer limit, String sort,
			String dir) {
		checkIfMapSharedWithUser(mapId);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Layer> cq = cb.createQuery(Layer.class);
		Root<Layer> rootLayer = cq.from(Layer.class);

		Join<Layer, User> LayerjoinUser = rootLayer.join("users", JoinType.LEFT);
		Join<Layer, Group> LayerjoinGroup = rootLayer.join("groups", JoinType.LEFT);

		TypedQuery<Layer> typedQuery = null;

		PageDto<LayerSimpleWithFieldsDto> pageDto = new PageDto<>();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		User user = userService.findByUsername(username);

		List<UUID> groupIds = new ArrayList<>();

		for (Group group : user.getGroups()) {
			groupIds.add(group.getId());
		}

		Predicate layersPre = null;
		Predicate ownersPre, finalPre = null;
		dz.eadn.sig.model.Map map = mapService.findById(mapId);

		List<UUID> layerIds = null;

		if (map != null) {
			layerIds = map.getLayers().stream().map(ml -> ml.getLayer().getId()).collect(Collectors.toList());
			if (layerIds.size() > 0)
				layersPre = cb.not(rootLayer.get("id").in(layerIds));
		}

		if (dir == "asc")
			cq.orderBy(cb.asc(rootLayer.get(sort)));
		else
			cq.orderBy(cb.desc(rootLayer.get(sort)));

		ownersPre = cb.or(cb.equal(LayerjoinUser.get("id"), user.getId()), LayerjoinGroup.get("id").in(groupIds));
		if (layersPre != null)
			finalPre = cb.and(ownersPre, layersPre);
		else
			finalPre = ownersPre;

		typedQuery = entityManager.createQuery(cq.select(rootLayer).distinct(true).where(finalPre));

		int total = typedQuery.getResultList().size();

		if (limit != -1)
			typedQuery.setFirstResult(page * limit).setMaxResults(limit);

		List<LayerSimpleWithFieldsDto> layerSimpleDtos = cModelMapper.mapList(typedQuery.getResultList(),
				LayerSimpleWithFieldsDto.class);

		pageDto.setContent(layerSimpleDtos);
		pageDto.setTotalElements(total);
		return pageDto;
	}

	@Override
	public LayerSimpleWithFieldsAndResourcesDto getLayerWithFieldsAndResources(UUID id, String mapSlug, String mode,
			boolean authenticated) {
		LayerSimpleWithFieldsAndResourcesDto withFieldsAndResourceDto = null;
		Layer layer = findById(id);
		if (mapSlug != null) {// In read mode
			CheckIfUserHasPrivilegeOnLayer(layer.getSlug(), mapSlug, "ENTITY_ELEMENT_READ_AUTHORITY", "read");
		} else {// In edition mode
			CheckIfUserHasPrivilegeOnLayer(layer.getSlug(), null, "LAYER_READ_AUTHORITY", "write");
		}
		if (layer != null) {

			withFieldsAndResourceDto = modelMapper.map(layer, LayerSimpleWithFieldsAndResourcesDto.class);
			if (!mode.equals("edit")) {
				if (authenticated == true) {

					List<FieldSimpleWithResourceDto> fields = withFieldsAndResourceDto.getFields().stream()
							.filter(field -> field.getVisible()).collect(Collectors.toList());
					withFieldsAndResourceDto.setFields(fields);

				} else {

					List<FieldSimpleWithResourceDto> fields = withFieldsAndResourceDto.getFields().stream()
							.filter(field -> field.getVisible()).filter(field -> field.getPublique())
							.filter(field -> field.getPublique()).collect(Collectors.toList());
					withFieldsAndResourceDto.setFields(fields);

				}

			}

		}

		return withFieldsAndResourceDto;
	}

	// @Override
	// public ResponseEntity updateStyle(String layerSlug, StyleDto styleDto) {
	// CheckIfUserHasPrivilegeOnLayer(layerSlug, null,
	// "CONFIGURE_LAYER_STYLE_AUTHORITY", "write");
	// Layer layer = layerRepository.findBySlug(layerSlug);
	// layer.setSymbologyType(styleDto.getSymbologyType());
	// layer.setLabelingEnabled(styleDto.isLabelEnabled());
	// if (styleDto.getIconUrl() != null) {
	// layer.setIconUrl(styleDto.getIconUrl());
	// }
	// try {
	// geoserverService.updateStyle(layer.getSlug(),
	// geoToolsService.createStyle(layer.getSlug(), layer.getTopo(),
	// layer.getSymbologyType(), styleDto));
	// ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
	// layer.setStyle(ow.writeValueAsString(styleDto));
	// layerRepository.save(layer);
	//
	// // prepare notifications
	//
	// List<User> users = new ArrayList<>();
	//
	// if (layer != null) {
	//
	// users.addAll(layer.getUsers());
	//
	// for (Group group : layer.getGroups()) {
	// users.addAll(group.getUsers());
	// }
	//
	// }
	//
	// Authentication authentication =
	// SecurityContextHolder.getContext().getAuthentication();
	//
	// String message =
	// String.format(messages.getMessages().get("NM_LAYER_CHANGE_STYLE"),
	// layer.getName(),
	// authentication.getName());
	//
	// SystemNotification systemNotification =
	// createSystemNotification(Transaction.UPDATE, null);
	//
	// NotificationSimpleDto notification =
	// createNotification(NotificationLevel.INFO, Operation.CHANGEMENT_STYLE,
	// message, systemNotification, users);
	//
	// notificationMessageService.sendNotificationMessage(notification);
	//
	// } catch (Exception e) {
	// throw new GlobalException("L'opération de mise à jour du style a échoué");
	// }
	// return new ResponseEntity(modelMapper.map(layer,
	// LayerSimpleWithFieldsDto.class), HttpStatus.ACCEPTED);
	// }

	// @Override
	// public String getStyle(String layerSlug, String mapSlug) {
	// if(mapSlug != null){
	// CheckIfUserHasPrivilegeOnLayer(layerSlug, mapSlug, null, "read");
	// }else{
	// CheckIfUserHasPrivilegeOnLayer(layerSlug, null, null, "write");
	// }
	// Layer layer = layerRepository.findBySlug(layerSlug);
	// try {
	// return layer.getStyle();
	// } catch (Exception e) {
	// throw new GlobalException("impossible de récupérer le style de la couche");
	// }
	// }

	@Override
	public PageDto<UserSimpleDto> getUsersSharingLayer(Layer layer, Integer page, Integer limit, String sort,
			String dir) {
		PageDto<UserSimpleDto> pageDto = null;
		if (layer != null) {
			pageDto = new PageDto<>();
			Sort sortDir = null;

			if (dir.equals("asc"))
				sortDir = Sort.by(sort).ascending();
			else
				sortDir = Sort.by(sort).descending();

			Pageable pageable = PageRequest.of(page, limit, sortDir);

			Page<User> usersPage = userRepository.findByLayers(layer, pageable);
			List<UserSimpleDto> userSimpleDtos = cModelMapper.mapList(usersPage.getContent(), UserSimpleDto.class);
			pageDto.setContent(userSimpleDtos);
			pageDto.setTotalElements(usersPage.getTotalElements());
		}

		return pageDto;
	}

	@Override
	public PageDto<UserCompleteDto> getUsersSharingLayerAutoComplete(String layerSlug, String name, Integer page,
			Integer limit, String sort, String dir) {
		PageDto<UserCompleteDto> pageDto = null;
		if (layerSlug != null) {
			pageDto = new PageDto<>();
			Sort sortDir = null;

			if (dir.equals("asc"))
				sortDir = Sort.by(sort).ascending();
			else
				sortDir = Sort.by(sort).descending();

			Pageable pageable = PageRequest.of(page, limit, sortDir);

			Page<User> usersPage = userRepository.findByFirstNameContainingAndLayersOrLastNameContainingAndLayers(name,
					layerRepository.findBySlug(layerSlug), name, layerRepository.findBySlug(layerSlug), pageable);
			List<UserCompleteDto> userSimpleDtos = cModelMapper.mapList(usersPage.getContent(), UserCompleteDto.class);
			pageDto.setContent(userSimpleDtos);
			pageDto.setTotalElements(usersPage.getTotalElements());
		}

		return pageDto;
	}

	@Override
	public PageDto<GroupSimpleDto> getGroupsSharingLayer(Layer layer, Integer page, Integer limit, String sort,
			String dir) {

		PageDto<GroupSimpleDto> pageDto = null;
		if (layer != null) {
			pageDto = new PageDto<>();
			Sort sortDir = null;

			if (dir.equals("asc"))
				sortDir = Sort.by(sort).ascending();
			else
				sortDir = Sort.by(sort).descending();

			Pageable pageable = PageRequest.of(page, limit, sortDir);

			Page<Group> groupsPage = groupRepository.findByLayers(layer, pageable);
			List<GroupSimpleDto> groupSimpleDto = cModelMapper.mapList(groupsPage.getContent(), GroupSimpleDto.class);
			pageDto.setContent(groupSimpleDto);
			pageDto.setTotalElements(groupsPage.getTotalElements());
		}
		return pageDto;
	}

	@Override
	public LayerSimpleWithOthersDto getLayerWithOthers(UUID id, String source, Integer page, Integer limit, String sort,
			String dir) {
		Layer layer = findById(id);
		LayerSimpleWithOthersDto withOthersDto = null;
		PageDto<UserSimpleDto> userPageDto = null;
		PageDto<GroupSimpleDto> groupPageDto = null;

		if (layer != null) {
			if (source.equals("users"))
				userPageDto = getUsersSharingLayer(layer, page, limit, sort, dir);
			else if (source.equals("groups"))
				groupPageDto = getGroupsSharingLayer(layer, page, limit, sort, dir);
			else if (source.equals("all")) {
				userPageDto = getUsersSharingLayer(layer, page, limit, sort, dir);
				groupPageDto = getGroupsSharingLayer(layer, page, limit, sort, dir);
			}

			withOthersDto = new LayerSimpleWithOthersDto();

			withOthersDto.setId(layer.getId());
			withOthersDto.setName(layer.getName());
			withOthersDto.setCreatedBy(layer.getCreatedBy());
			withOthersDto.setUsers(userPageDto);
			withOthersDto.setGroups(groupPageDto);
		}

		return withOthersDto;
	}

	@Override
	public boolean CheckIfUserHasPrivilegeOnLayer(String layerSlug, String mapSlug, String permission, String mode) {
		UserDetails userDetails = null;
		try {
			userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} catch (Exception e) {

		}
		Layer layer = layerRepository.findBySlug(layerSlug);
		if (mode.equals("write")) {
			if (userDetails == null) {
				throw new AccessNotPermittedException(
						"vous ne disposez pas de suffisamment de privilèges pour effectuer cette action !");
			} else {
				Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
				// Check if the user belongs to the admin group
				if (user.get().getGroups().stream().filter(g -> g.getName().equals("ROLE_ADMIN"))
						.collect(Collectors.toList()).size() > 0) {
					return true;
				}
				// Check if the user has the permission to edit style
				if (permission != null & SecurityContextHolder.getContext().getAuthentication().getAuthorities()
						.stream().filter(a -> a.getAuthority().equals(permission)).collect(Collectors.toList())
						.size() == 0) {
					throw new AccessNotPermittedException(
							"vous ne disposez pas de suffisamment de privilèges pour effectuer cette action !");
				}
				// Check if the layer shared with this user
				if (layer.getUsers().stream().filter(u -> u.getId().equals(user.get().getId()))
						.collect(Collectors.toList()).size() > 0) {
					return true;
				} else {
					// Check if the layer shared with user's groups
					for (int i = 0; i < layer.getGroups().size(); i++) {
						if (user.get().getGroups().contains(layer.getGroups().get(i))) {
							return true;
						}
					}
				}
			}
		} else if (mode.equals("read")) {
			if (layer.getMaps().stream()
					.filter(m -> m.getMap().getPrivacy().equals(Privacy.PUBLIC)
							|| m.getMap().getPrivacy().equals(Privacy.PUBLIC_WITH_LINK))
					.collect(Collectors.toList()).size() > 0) {
				return true;
				// if the user is authenticated
			} else if (userDetails != null) {
				Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
				// Check if the user belongs to the admin group
				if (user.get().getGroups().stream().filter(g -> g.getName().equals("ROLE_ADMIN"))
						.collect(Collectors.toList()).size() > 0) {
					return true;
				}
				// Check if the user has the permission
				if (permission != null & SecurityContextHolder.getContext().getAuthentication().getAuthorities()
						.stream().filter(a -> a.getAuthority().equals(permission)).collect(Collectors.toList())
						.size() == 0) {
					throw new AccessNotPermittedException(
							"vous ne disposez pas de suffisamment de privilèges pour effectuer cette action !");
				}
				if (user.get().getLayers().stream().anyMatch(l -> l.getSlug().equals(layer.getSlug()))) {
					return true;
				} else if (layerRepository.checkIfLayerSharedWithUserGroups(user.get().getId(), layer.getId()) > 0) {
					return true;
				} else if (layerRepository.checkIfLayerBelongsToUserMap(user.get().getId(), mapSlug,
						layer.getId()) > 0) {
					return true;
				} else if (layerRepository.checkIfLayerBelongsToUserGroupsMap(user.get().getId(), mapSlug,
						layer.getId()) > 0) {
					return true;
				}
			}
		}
		throw new AccessNotPermittedException(
				"vous ne disposez pas de suffisamment de privilèges pour effectuer cette action !");
	}

	@Override
	public boolean CheckIfUserHasPrivilegeOnLayerAndEntityElement(String layerSlug, String mapSlug, String permission,
			String mode, UUID entityElementId) {
		if (entityElementId != null && CheckIfUserHasPrivilegeOnLayer(layerSlug, mapSlug, permission, mode)
				&& entityElementService.isAuthorizedArea(entityElementService.findById(entityElementId).getGeom())) {
			return true;
		} else {
			throw new AccessNotPermittedException(
					"vous ne disposez pas de suffisamment de privilèges pour effectuer cette action !");
		}
	}

	@Override
	public List<ButtonPermission> CheckIfUserHasPrivilegeOnLayerAndEntityElements(
			List<ButtonPermission> buttonPermissions) {
		for (ButtonPermission buttonPermission : buttonPermissions) {
			try {
				CheckIfUserHasPrivilegeOnLayerAndEntityElement(buttonPermission.getLayerSlug(), null,
						buttonPermission.getPermission(), "write", buttonPermission.getEntityElementId());
				buttonPermission.setIsAllowed(true);
			} catch (Exception e) {
				buttonPermission.setIsAllowed(false);
			}
		}
		return buttonPermissions;
	}

	@Override
	public boolean checkIfMapSharedWithUser(UUID mapId) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
		// Check if the user belongs to Admin group
		if (user.get().getGroups().stream().filter(g -> g.getName().equals("ROLE_ADMIN")).collect(Collectors.toList())
				.size() > 0) {
			return true;
		}
		// Check if the map shared with user
		if (user.get().getMaps().stream().filter(m -> m.getId().equals(mapId)).collect(Collectors.toList())
				.size() > 0) {
			return true;
		} else {
			// Check if the map shared with user's groups
			for (int i = 0; i < user.get().getGroups().size(); i++) {
				if (user.get().getGroups().get(i).getMaps().stream().filter(m -> m.getId().equals(mapId))
						.collect(Collectors.toList()).size() > 0) {
					return true;
				}
			}
		}
		throw new AccessNotPermittedException(
				"vous ne disposez pas de suffisamment de privilèges pour effectuer cette action !");
	}

	public NotificationSimpleDto createNotification(NotificationLevel level, Operation operation, String message,
			SystemNotification systemNotification, List<User> users) {

		NotificationSimpleDto notification = new NotificationSimpleDto();

		notification.setObject(NotificationObject.Couche);
		notification.setLevel(level);
		notification.setOperation(operation);
		notification.setDestination("layer");
		notification.setMessage(message);
		notification.setSystemNotification(systemNotification);
		notification.setUsers(users);

		return notification;
	}

	public SystemNotification createSystemNotification(Transaction transaction, Object object) {
		SystemNotification systemNotification = new SystemNotification();

		systemNotification.setType("layers");
		systemNotification.setTransaction(transaction);
		systemNotification.setObject(object);

		return systemNotification;
	}

	public String generateQuery(String fieldType, SearchCriteria criteria, boolean fullQuery) {
		return buildQuery(fieldType, criteria, fullQuery);
	}

	@Override
	public LayerDto cloneLayer(String layerSlug, CloneLayerDto cloneLayerDto) {
		Layer layer = layerRepository.findBySlug(layerSlug);
		LayerDto clonedLayer = layerMapper.entityToDto(layer);
		clonedLayer.setName(cloneLayerDto.getOutputName());
		clonedLayer.setId(null);
		clonedLayer.getFieldDtos().forEach(f -> f.setId(null));

		LayerDto savedLayer = save(clonedLayer);
		ShareLayerWithOthers shareLayerWithOthers = new ShareLayerWithOthers();
		if (cloneLayerDto.getCloneUsers()) {
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			List<UserDto> userDtoList = cModelMapper.mapList(layer.getUsers(), UserDto.class);
			userDtoList.forEach(u -> {
				u.setIsNew(true);
				u.setPassword(null);
			});
			shareLayerWithOthers.setUsers(
					userDtoList.stream().filter(u -> !u.getUsername().equals(username)).collect(Collectors.toList()));
		}
		if (cloneLayerDto.getCloneGroups()) {
			List<GroupDto> groupDtoList = cModelMapper.mapList(layer.getGroups(), GroupDto.class);
			groupDtoList.forEach(m -> m.setIsNew(true));
			shareLayerWithOthers.setGroups(groupDtoList);
		}
		shareLayer(savedLayer.getId(), shareLayerWithOthers);
		String query = "INSERT INTO sig.entity_element(id, \n" +
				"\t create_date, created_by, deleted, last_modified_date, modified_by, geom, properties, layer_entity_element)\n"
				+
				"\tselect md5(random()::text || clock_timestamp()::text)::uuid, e.create_date, e.created_by, e.deleted, e.last_modified_date, e.modified_by, e.geom, e.properties, '"
				+ savedLayer.getId() + "' from sig.entity_element e where e.layer_entity_element =  '" + layer.getId()
				+ "'";
		jdbcTemplate.execute(query);
		return savedLayer;
	}

	@Override
	public Boolean checkIfLayerHasData(UUID layerId) {
		String query = "select 1 as contain where exists (select e.id from sig.entity_element e where e.deleted = false and e.layer_entity_element = ?)";
		try {
			jdbcTemplate.queryForObject(query, new Object[] { layerId }, String.class);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public List<LayerSimpleDto> findByTypeLimit(TypeLimit typeLimit) {
		List<LayerSimpleDto> layerSimpleDtos = null;
		List<Layer> layers = layerRepository.findByTypeLimit(typeLimit);

		if (layers != null) {
			layerSimpleDtos = cModelMapper.mapList(layers, LayerSimpleDto.class);
		}

		return layerSimpleDtos;
	}
}