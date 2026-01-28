package dz.eadn.sig.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintDeclarationException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import dz.eadn.sig.model.*;
import org.codehaus.janino.Java;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.util.Classes;
import org.hibernate.exception.ConstraintViolationException;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import dz.eadn.sig.constants.Constants;
import dz.eadn.sig.dto.CommonFilter;
import dz.eadn.sig.dto.EntityElementDto;
import dz.eadn.sig.dto.EntityElementSimpleDto;
import dz.eadn.sig.dto.GlobalFilterDto;
import dz.eadn.sig.dto.LayerDto;
import dz.eadn.sig.dto.NotificationMessagesDto;
import dz.eadn.sig.dto.NotificationSimpleDto;
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.dto.SystemNotification;
import dz.eadn.sig.dto.Transaction;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.exceptions.GlobalException;
import dz.eadn.sig.mapper.CommonModelMapper;
import dz.eadn.sig.mapper.EntityElementSlugMapper;
import dz.eadn.sig.mapper.LayerUserMapper;
import dz.eadn.sig.repository.EntityElementRepository;
import dz.eadn.sig.repository.LayerRepository;
import dz.eadn.sig.service.EntityElementService;
import dz.eadn.sig.service.FieldService;
import dz.eadn.sig.service.GeoserverService;
import dz.eadn.sig.service.LayerService;
import dz.eadn.sig.service.NotificationMessageService;
import dz.eadn.sig.service.UploadFileService;
import dz.eadn.sig.service.UserService;
import dz.eadn.sig.service.common.impl.CommonServiceImpl;
import dz.eadn.sig.util.EntityElementReader;
import dz.eadn.sig.util.EntityElementWriter;
import dz.eadn.sig.util.SLDGeneratorImpl;
import dz.eadn.sig.util.SearchCriteria;
import dz.eadn.sig.util.Utils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour && Ameur LAMOUR && LOKBANI Chouaib
 *
 */
@Slf4j
@Service
public class EntityElementServiceImpl extends CommonServiceImpl<EntityElement, EntityElementDto>
		implements EntityElementService {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private EntityElementRepository entityElementRepository;

	@Autowired
	private List<EntityElementReader> readers;

	@Autowired
	private List<EntityElementWriter> writers;

	@Autowired
	private LayerRepository layerRepository;

	@Autowired
	private LayerService layerService;

	@Autowired
	private GeoserverService geoserverService;

	@Autowired
	private FieldService fieldService;

	@Autowired
	private UserService userService;

	@Autowired
	private UploadFileService uploadFileService;

	@Autowired
	private EntityElementSlugMapper mapper;

	@Autowired
	private CommonModelMapper<?, ?> cModelMapper;

	@Autowired
	@Qualifier("layerUserMapper")
	private LayerUserMapper luMapper;

	@Autowired
	private NotificationMessagesDto messages;

	@Autowired
	private NotificationMessageService notificationMessageService;

	public EntityElementServiceImpl() {
		super(EntityElement.class);
	}

	@Override
	public void delete(UUID id) {
		EntityElement entityElement = findById(id);
		// The second check after ENTITY_ELEMENT_DELETE_AUTHORITY
		layerService.CheckIfUserHasPrivilegeOnLayer(entityElement.getLayer().getSlug(), null, null, "write");
		if (entityElement == null) {
			throw new EntityNotFoundException("Impossible de supprimer l'entit� " + domainClass.getSimpleName());
		}
		try {
			entityElementRepository.delete(entityElement);
			String path = "/" + Constants.FOLDER_IMAGES_LAYERS + "/" + entityElement.getLayer().getId().toString();
			uploadFileService.deleteFolder(path, entityElement.getId().toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
			throw new GlobalException("Cet enregistrement est utilisé dans une autre relation\n");
		}
	}

	@Override
	public EntityElementReader getReader(String dataName) {
		for (EntityElementReader reader : readers) {
			if (reader.dataName().equals(dataName))
				return reader;
		}
		return null;
	}

	@Override
	public EntityElementWriter getWriter(String dataName) {
		for (EntityElementWriter writer : writers) {
			if (writer.dataName().equals(dataName))
				return writer;
		}
		return null;
	}


	@Override
	public Layer importEntityElementsFromFeatureCollection(LayerDto layerDto, SimpleFeatureCollection fc)
			throws Exception {
		Layer layer = createLayerFromSchema(layerDto, fc.getSchema());

		SimpleFeatureIterator it = fc.features();

		while (it.hasNext()) {
			SimpleFeature feature = it.next();
			EntityElement entityElement = featureToEntityElement(layer, feature, false);
			entityElementRepository.save(entityElement);
		}

		return layer;
	}

	public FieldType getFieldType(PropertyType propretyType) {
		String className = Classes.getShortName(propretyType.getBinding());
		switch (className) {
		case "Long":
			return FieldType.NUMBER;
		case "Integer":
			return FieldType.INTEGER;
		case "Double":
			return FieldType.NUMBER;
		case "String":
			return FieldType.TEXT;
		case "Date":
			return FieldType.DATE;
		default:
			return FieldType.TEXT;
		}
	}

	@Transactional
	public Layer createLayerFromSchema(LayerDto layerDto, SimpleFeatureType simpleFeatureType) {

		String layerSlug = Utils.toSlug(layerDto.getName());
		Layer layer = layerRepository.findBySlug(layerSlug);
		if (layer == null) {
			layer = new Layer();
			layer.setName(layerDto.getName());
			layer.setSlug(layerSlug);
			layer.setFields(new ArrayList<Field>());
		} else {
			if (layer.getDeleted())
				layer.setDeleted(false);
		}
		int order = 1;
		for (PropertyDescriptor proprety : simpleFeatureType.getDescriptors()) {
			if (proprety instanceof GeometryDescriptor) {
				layer.setTopo(Classes.getShortName(proprety.getType().getBinding()));
			} else {
				String fieldName = proprety.getName().getLocalPart();

				if (fieldName.equals("id")) {
					continue;
				}

				String fieldSlug = Utils.toSlug(fieldName);

				Field field;

				if (layer.getId() == null) {
					field = new Field();
					field.setType(getFieldType(proprety.getType()));
					field.setName(fieldName);
					field.setOrder(order++);
					field.setSlug(fieldSlug);
					field.setRequired(false);
					field.setVisible(true);
					field.setLayer(layer);
					layer.getFields().add(field);
				} else {
					boolean exists = fieldService.findBySlugAndLayer(fieldSlug, layer);
					if (exists) {
						field = new Field();
						field.setType(getFieldType(proprety.getType()));
						field.setName(fieldName);
						field.setOrder(order++);
						field.setSlug(fieldSlug);
						field.setRequired(false);
						field.setVisible(true);
						layer.getFields().add(field);
					}
				}

			}
		}

		if (!layer.getFields().isEmpty())
			layer.setIdentifiant(layer.getFields().get(0).getSlug());

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		User owner = userService.findByUsername(username);
		layer.getUsers().add(owner);
		layerRepository.save(layer);
		LayerDto saveLayerDto = luMapper.entityToDto(layer);

		// prepare notifications

		String message = String.format(messages.getMessages().get("NM_LAYER_CREATE"), saveLayerDto.getName(),
				authentication.getName());

		SystemNotification systemNotification = createSystemNotification(Transaction.ADD, saveLayerDto);

		NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.CREATION, message,
				systemNotification, new ArrayList<>());

		notificationMessageService.sendNotificationMessage(notification);

		// Create data in goeserver and view as well
		try {
			layerService.createSqlView(layer);
			geoserverService.addLayer(new SLDGeneratorImpl().createFeatureType(saveLayerDto));
		} catch (Exception e) {
			layerService.setForceDelete(true);
			layerService.delete(layer.getId());
			layerService.setForceDelete(false);
			throw new GlobalException("l'opération d'ajout de couche a échoué !");
		}

		return layer;
	}

	public List<EntityElement> featuresToEntityElements(Layer layer, List<SimpleFeature> features, boolean withSlug) {
		return features.stream().map(feature -> featureToEntityElement(layer, feature, withSlug))
				.collect(Collectors.toList());
	}

	@Override
	public EntityElement featureToEntityElement(String layerSlug, SimpleFeature feature, boolean withSlug) {
		Layer layer = layerRepository.findBySlug(layerSlug);
		return featureToEntityElement(layer, feature, withSlug);
	}

	@Override
	public EntityElement featureToEntityElement(Layer layer, SimpleFeature feature, boolean withSlug) {
		EntityElement entityElement = new EntityElement();
		entityElement.setLayer(layer);

		if (feature.getDefaultGeometry() != null) {
			Geometry geometry = (Geometry) feature.getDefaultGeometry();
			geometry.setSRID(4326);
			entityElement.setGeom(geometry);
		}

		Map<String, String> properties = new HashMap<String, String>();
		Object object = null;
		for (Field field : layer.getFields()) {
			if (withSlug)
				object = feature.getAttribute(field.getSlug());
			else
				object = feature.getAttribute(field.getName());
			if (object == null)
				continue;
			// to store uuid field.getId().toString()
			properties.put(field.getSlug(), object.toString());
		}

		entityElement.setProperties(properties);
		return entityElement;
	}

	@Override
	public PageDto<EntityElementDto> findByAdvancedFilter(CommonFilter filter, String layerSlug, Integer page,
			Integer limit, String sort, String dir) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<EntityElement> cq = cb.createQuery(EntityElement.class);
		Root<EntityElement> root = cq.from(EntityElement.class);

		Geometry geometry = null;

		List<Predicate> predicates = new ArrayList<>();

		PageDto<EntityElementDto> pageDto = new PageDto<>();

		if (dir.equals("asc"))
			cq.orderBy(cb.asc(root.get(sort)));
		else
			cq.orderBy(cb.desc(root.get(sort)));

		Predicate deleted = cb.equal(root.get("deleted"), false);


		Predicate layer = cb.equal(root.get("layer"), layerRepository.findBySlug(layerSlug));

		Predicate commonPredicate = cb.and(deleted, layer);

		Predicate resultPredicate = null;

		Layer l = layerRepository.findBySlug(layerSlug);

		for (SearchCriteria criteria : filter.getRules()) {

			try {

				if (criteria.getField().equals("spatial")) {
					Optional<EntityElement> optional = entityElementRepository
							.findById(UUID.fromString(criteria.getValue()));

					if (optional.isPresent())
						geometry = optional.get().getGeom();

					resultPredicate = findBySpatial(criteria, root, geometry);

					if (resultPredicate != null)
						predicates.add(resultPredicate);
				} else if (criteria.getField() == "createDate" || criteria.getField() == "lastModifiedDate") {
					resultPredicate = findByProperty(criteria, cb, root);

					if (resultPredicate != null)
						predicates.add(resultPredicate);
				} else {
					resultPredicate = findByField(criteria, root, l);
					if (resultPredicate != null)
						predicates.add(resultPredicate);
				}

			} catch (Exception e) {
				log.info(e.getMessage());
			}

		}

		Predicate searchPredicate = null;

		if (filter.getCondition().equals("or"))
			searchPredicate = cb.or(predicates.toArray(new Predicate[] {}));
		else
			searchPredicate = cb.and(predicates.toArray(new Predicate[] {}));

		Predicate finalPredicate = null;

		finalPredicate = cb.and(searchPredicate, commonPredicate);

		TypedQuery<EntityElement> typedQuery = entityManager.createQuery(cq.select(root).where(finalPredicate));

		int total = typedQuery.getResultList().size();

		if (limit != -1)
			typedQuery.setFirstResult(page * limit).setMaxResults(limit);

		List<EntityElement> entityElements = typedQuery.getResultList();

		pageDto.setContent(mapper.entitysToDtos(entityElements));
		pageDto.setTotalElements(total);

		return pageDto;

	}

	@Override
	public PageDto<EntityElementDto> findNearestEntityElements(UUID selectedEntityElement, UUID targetLayer, String targetLayerSlug, String mapSlug,
			double perimeter, String unit, String geometry, PostgisOperation operation,boolean intersection, PostgisOperation algebraOperation,
			@PageableDefault(size = 10, page = 0) Pageable pageable) {
		layerService.CheckIfUserHasPrivilegeOnLayer(targetLayerSlug, mapSlug, "ENTITY_ELEMENT_READ_AUTHORITY", "read");
		PageDto<EntityElementDto> pageDto = new PageDto<>();
		Page<EntityElement> entityElementPage = null;
		try {
			if(intersection){
				switch (algebraOperation){
					case INTERSECTION:
						entityElementPage = entityElementRepository.findEntityElementByIntersection(geometry, targetLayer, pageable);
						break;
					case COVER:
						entityElementPage = entityElementRepository.findEntityElementByCover(geometry, targetLayer, pageable);
						break;
				}
			}else{
				switch (operation) {
					case DWITHIN:
						entityElementPage = entityElementRepository.findEntityElementByDwithin(geometry,
								calculePerimeter(unit, perimeter), targetLayer, selectedEntityElement, pageable);
						break;

					case BEYOND:
						entityElementPage = entityElementRepository.findEntityElementByBeyond(geometry,
								calculePerimeter(unit, perimeter), targetLayer, selectedEntityElement, pageable);
						break;

				}
			}
			pageDto.setContent(mapper.entitysToDtos(entityElementPage.getContent()));
			pageDto.setTotalElements(entityElementPage.getTotalElements());
			return pageDto;
		} catch (Exception e) {
			throw new GlobalException("une erreur inattendue s'est produite");
		}
	}

	double calculePerimeter(String unit, double perimeter) {
		switch (unit) {
		case "kilometers":
			return perimeter * 1000;
		case "miles":
			return perimeter * 1609.34;
		case "feet":
			return perimeter * 0.3048;
		default:
			return perimeter;
		}
	}

	@Override
	public List<String> findAllByProperty(String fieldName, UUID layerId) {
		try{
			String query = String.format(
					"SELECT e.properties->>'%s' FROM sig.entity_element e where e.layer_entity_element = '%s' and e.deleted = false",
					fieldName.replaceAll("'", "''"), layerId.toString());
			List<String> resultList = jdbcTemplate.queryForList(query, String.class);
			return resultList;
		}catch (Exception e){
			e.getMessage();
			throw new GlobalException(e.getMessage());
		}
	}

	@Override
	public List<String> groupByProperty(String fieldName, UUID layerId) {
		try{
			String query = String.format(
					"SELECT e.properties->>'%s' FROM sig.entity_element e where e.layer_entity_element = '%s' and e.deleted = false group by e.properties->>'%s'",
					fieldName.replaceAll("'", "''"), layerId.toString(), fieldName.replaceAll("'", "''"));
			List<String> resultList = jdbcTemplate.queryForList(query, String.class);
			return resultList;
		}catch (Exception e){
			e.getMessage();
			throw new GlobalException(e.getMessage());
		}
	}

	@Override
	public List<Map<String, Object>> getEntityElementForReporting(String layerSlug) {
		layerService.CheckIfUserHasPrivilegeOnLayer(layerSlug, null, "REPORTING", "write");
		String query = "SELECT ";
		List<FieldType> notIncludedReportingFieldType = new ArrayList<>();
		notIncludedReportingFieldType.add(FieldType.IMAGE);
		notIncludedReportingFieldType.add(FieldType.CAROUSEL);
		notIncludedReportingFieldType.add(FieldType.MULTI_SELECT);
		notIncludedReportingFieldType.add(FieldType.HTMLEDITOR);
		notIncludedReportingFieldType.add(FieldType.TEXTAREA);
		Layer layer = layerRepository.findBySlug(layerSlug);
		Map<String, Object> fields = new HashMap<>();
		for (int i = 0; i < layer.getFields().size(); i++) {
			if (!notIncludedReportingFieldType.contains(layer.getFields().get(i).getType())) {
				fields.put(layer.getFields().get(i).getSlug(), layer.getFields().get(i).getType());
				if (fields.size() > 1) {
					query += " , ";
				}
				query += "e.properties->>'" + layer.getFields().get(i).getSlug().replaceAll("'", "''") + "' as  " + '"'
						+ layer.getFields().get(i).getName() + '"';
			}
		}
		query += " FROM sig.entity_element e where e.layer_entity_element = '" + layer.getId()
				+ "' and e.deleted = false";
		try {
			List<Map<String, Object>> result = jdbcTemplate.queryForList(query);
			result.add(fields);
			return result;
		} catch (Exception e) {
			e.getMessage();
			new GlobalException("Impossible de récupérer les données");
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getEntityElementForSecurityRestrictions(UUID layerId, String identifiant) {
		if(identifiant != null){
			String query = "select e.id, e.properties->> '"+ identifiant +"' as text from sig.entity_element e where e.deleted = false and e.layer_entity_element='" + layerId + "'";
			try{
				return jdbcTemplate.queryForList(query);
			}catch (Exception e){
				throw new GlobalException("une erreur inattendue s'est produite");
			}
		}else{
			throw new GlobalException("Veuillez définir un identifiant pour cette couche");
		}
	}

	@Override
	public List<Map<String, Object>> findAllByProperty(String fieldName, UUID layerId,
			CLASSIFICATIONMODE classficationMode, int classes) {
		if (fieldName == null)
			throw new GlobalException("Veuillez sélectionner un champ !");
		if (classficationMode == null)
			throw new GlobalException("Veuillez sélectionner un mode !");

		switch (classficationMode) {
		case EQUAL_COUNT:
			String equalCountQuery = "select min(cast(grouped.prop as float)) as minValuePerInterval, max(cast(grouped.prop as float)) as maxValuePerInterval from (SELECT e.properties->>'"
					+ fieldName + "' as prop, NTILE(" + classes + ") over (order by cast(e.properties->>'" + fieldName
					+ "' as float ) asc) as ntile\n" + "FROM sig.entity_element e where e.layer_entity_element ='"
					+ layerId + "' and e.deleted = false and length(trim(e.properties->>'" + fieldName
					+ "' )) > 0)  grouped group by grouped.ntile order by minValuePerInterval";
			try {
				List<Map<String, Object>> equalCountResultList = jdbcTemplate.queryForList(equalCountQuery);
				return equalCountResultList;
			} catch (Exception e) {
				e.printStackTrace();
				throw new GlobalException("Impossible de classer les données !");
			}
		case LOGARITHMIC_SCALE:
			break;
		case STANDARD_DEVIATION:
			break;
		case PRETTY_BREAKS:
			break;
		case NATURAL_BREAKS:
			break;
		default:
			String getIntervalLengthQuery = "SELECT min(cast(e.properties->>'" + fieldName
					+ "' as float)), max(cast (e.properties->>'" + fieldName
					+ "' as float)) - min(cast(e.properties->>'" + fieldName + "' as float)) as intervalLength\n"
					+ "FROM sig.entity_element e where e.layer_entity_element ='" + layerId
					+ "' and e.deleted = false and length(trim(e.properties->>'" + fieldName + "' )) > 0";

			List<Map<String, Object>> result;
			try {
				result = jdbcTemplate.queryForList(getIntervalLengthQuery);
			} catch (Exception e) {
				e.printStackTrace();
				throw new GlobalException("Impossible de classer les données !");
			}
			int index = 1;
			List<Map<String, Object>> equalIntervalResultList = new ArrayList<>();

			while (index <= classes) {
				Double minValue = (Double) result.get(0).get("min")
						+ ((Double) result.get(0).get("intervalLength") / classes) * (index - 1);
				Double maxValue = (Double) result.get(0).get("min")
						+ ((Double) result.get(0).get("intervalLength") / classes) * index;
				Map<String, Object> intervalClass = new HashMap<>();
				intervalClass.put("minvalueperinterval", minValue);
				intervalClass.put("maxvalueperinterval", maxValue);
				equalIntervalResultList.add(intervalClass);
				index += 1;
			}
			return equalIntervalResultList;

		}
		return null;
	}

	@Override
	public List<Map<String, Object>> findPropertyByLayer(String fieldName, String layerSlug) {
		try{
			String query = "SELECT ee.id as value, ee.properties->> '" + fieldName
					+ "' as text FROM sig.entity_element ee WHERE layer_entity_element = (SELECT l.id FROM sig.layer l WHERE l.slug = '"
					+ layerSlug + "')";

			List<Map<String, Object>> result = jdbcTemplate.queryForList(query);

			return result;
		}catch (Exception e){
			e.getMessage();
			throw new GlobalException(e.getMessage());
		}
	}

	@Override
	public String getConvexHull(String multipoint) {
		try{
			String query = "SELECT ST_AsText(ST_ConvexHull(ST_Collect(ST_GeomFromText('MULTIPOINT(" + multipoint
					+ ")')))) from sig.entity_element";

			List<String> result = jdbcTemplate.queryForList(query, String.class);

			return result.get(0);
		}catch (Exception e){
			e.getMessage();
			throw new GlobalException(e.getMessage());
		}

	}

	@Override
	public String getCentroId(UUID id) {
		String query = "SELECT ST_AsText(ST_Centroid(e.geom)) FROM sig.entity_element e WHERE e.id ='" + id + "'";

		List<String> result = jdbcTemplate.queryForList(query, String.class);

		return result.get(0);
	}

	@Override
	public boolean isAuthorizedArea(Geometry geometry) {

		boolean authorized = false;
		String geomArray = "SELECT ST_Covers((SELECT ST_AsText(ST_Union(ARRAY[";

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			String username = authentication.getName();

			User user = userService.findByUsername(username);

			if (user != null && !userService.isAdministrateur(user.getUsername())) {

				List<EntityElement> entityElements = user.getEntityElements();

				if (entityElements != null && entityElements.size() > 0) {

					for (EntityElement entityElement : entityElements) {

						geomArray += "(SELECT e.geom FROM sig.entity_element e WHERE e.id='" + entityElement.getId()
								+ "')" + ",";
					}
					geomArray = geomArray.substring(0, geomArray.length() - 1);
					geomArray += "]))),'";
					geomArray += geometry + "')";
					List<Boolean> result = jdbcTemplate.queryForList(geomArray, Boolean.class);

					authorized = result.get(0);

				}
			}
			else 
				authorized=true;

		}
		return authorized;
	}

	public PageDto<EntityElement> findAllBySearch(GlobalFilterDto globalFilterDto, Integer page, Integer limit,
			String sort, String dir) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<EntityElement> cq = cb.createQuery(EntityElement.class);
		Root<EntityElement> root = cq.from(EntityElement.class);

		List<Predicate> predicates = new ArrayList<>();

		PageDto<EntityElement> pageDto = new PageDto<>();

		Predicate resultPre = null;

		Predicate deletedPre = cb.equal(root.get("deleted"), false);

		predicates.add(deletedPre);

		List<Predicate> layersPre = new ArrayList<>();
		String layerId = "";
		for (String layerSlug : globalFilterDto.getLayersSlug()) {
			Predicate layerPre = cb.equal(root.get("layer"), layerRepository.findBySlug(layerSlug));
			layersPre.add(layerPre);
		}

		Predicate layerFinal = cb.or(layersPre.toArray(new Predicate[] {}));

		predicates.add(layerFinal);


		try {
			String query = "SELECT e.id FROM sig.entity_element e WHERE ( e.properties->>";
			int i = 0;
			if (globalFilterDto != null) {
				List<String> fieldsSlug = globalFilterDto.getFieldsSlug();
				for (String fieldSlug : fieldsSlug) {
					if (i++ == fieldsSlug.size() - 1)
						query += "'" + fieldSlug.replaceAll("'", "''") + "' ILIKE '%"
								+ globalFilterDto.getSearchText().toLowerCase().replace("'", "''") + "%'";
					else
						query += "'" + fieldSlug.replaceAll("'", "''") + "' ILIKE '%"
								+ globalFilterDto.getSearchText().toLowerCase().replace("'", "''") + "%' OR e.properties->>";
				}
			}

			query += ") and e.layer_entity_element in " + globalFilterDto.getLayerIds();

			List<UUID> uuids = jdbcTemplate.queryForList(query, UUID.class);

			Expression<UUID> exp = root.get("id");

			if (uuids != null)
				resultPre = exp.in(uuids);

			predicates.add(resultPre);

			Predicate finalPre = cb.and(predicates.toArray(new Predicate[] {}));

			TypedQuery<EntityElement> typedQuery = entityManager.createQuery(cq.select(root).where(finalPre));

			int total = typedQuery.getResultList().size();

			if (limit != -1)
				typedQuery.setFirstResult(page * limit).setMaxResults(limit);

			List<EntityElement> entityElements = typedQuery.getResultList();

			pageDto.setContent(entityElements);
			pageDto.setTotalElements(total);

		} catch (Exception e) {
			log.info(e.getMessage());
		}

		return pageDto;
	}

	@Override
	public PageDto<EntityElementSimpleDto> findAllByLayer(String layerSlug, Integer page, Integer limit, String sort,
			String dir) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<EntityElement> cq = cb.createQuery(EntityElement.class);
		Root<EntityElement> root = cq.from(EntityElement.class);

		PageDto<EntityElementSimpleDto> pageDto = new PageDto<>();

		if (dir.equals("asc"))
			cq.orderBy(cb.asc(root.get(sort)));
		else
			cq.orderBy(cb.desc(root.get(sort)));

		Predicate deleted = cb.equal(root.get("deleted"), false);

		Predicate layer = cb.equal(root.get("layer"), layerRepository.findBySlug(layerSlug));

		Predicate commonPredicate = cb.and(deleted, layer);

		TypedQuery<EntityElement> typedQuery = entityManager.createQuery(cq.select(root).where(commonPredicate));

		int total = typedQuery.getResultList().size();

		if (limit != -1)
			typedQuery.setFirstResult(page * limit).setMaxResults(limit);

		List<EntityElement> entityElements = typedQuery.getResultList();

		List<EntityElementSimpleDto> entityElementSimpleDtos = cModelMapper.mapList(entityElements,
				EntityElementSimpleDto.class);

		for (EntityElementSimpleDto entityElementSimpleDto : entityElementSimpleDtos) {

			Layer myLayer = layerRepository.findBySlug(layerSlug);

			Map<String, String> properties = new LinkedHashMap<>();

			if (myLayer != null) {
				List<Field> fields = myLayer.getFields().stream().filter(f -> !f.getType().equals(FieldType.IMAGE))
						.filter(f -> !f.getType().equals(FieldType.CAROUSEL))
						.sorted(Comparator.comparingInt(Field::getOrder)).collect(Collectors.toList());

				fields.forEach(f -> {
					properties.put(f.getSlug(), entityElementSimpleDto.getProperties().get(f.getSlug()));
				});

				entityElementSimpleDto.setProperties(properties);
			}

		}

		pageDto.setContent(entityElementSimpleDtos);
		pageDto.setTotalElements(total);

		return pageDto;

	}

	@Override
	public String getBuffer(List<String> center, float radius) {

		String point = "'POINT (" + center.get(0) + " " + center.get(1) + ")'";

		String query = "SELECT ST_AsText(ST_Buffer(ST_GeomFromText(" + point + ",4326), " + radius + "))";

		List<String> result = jdbcTemplate.queryForList(query, String.class);

		return result.get(0);
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

	@Override
	public PageDto<EntityElementSimpleDto> findAllBySearchInViewTable(String search, String layerSlug, String mapSlug, Integer page,
			Integer limit, String sort, String dir) {
		layerService.CheckIfUserHasPrivilegeOnLayer(layerSlug, mapSlug, "ENTITY_ELEMENT_READ_AUTHORITY", "read");
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<EntityElement> cq = cb.createQuery(EntityElement.class);
		Root<EntityElement> root = cq.from(EntityElement.class);

		List<Predicate> predicates = new ArrayList<>();

		PageDto<EntityElementSimpleDto> pageDto = new PageDto<>();

		Predicate resultPre = null;
		Layer l = layerRepository.findBySlug(layerSlug);
		Predicate layerPre = cb.equal(root.get("layer"), l);
		Predicate deletedPre = cb.equal(root.get("deleted"), false);

		predicates.add(deletedPre);
		predicates.add(layerPre);

		try {

			Layer layer = layerService.findBySlug(layerSlug);

			String query = "SELECT e.id FROM sig.entity_element e WHERE ( e.properties->>";
			int i = 0;
			if (layer != null) {
				List<String> fieldsSlug = layer.getFields().stream().map(f -> f.getSlug()).collect(Collectors.toList());
				for (String fieldSlug : fieldsSlug) {
					if (i++ == fieldsSlug.size() - 1)
						query += "'" + fieldSlug.replaceAll("'", "''") + "' ILIKE '%" + search.toLowerCase().replace("'", "''") + "%'";
					else
						query += "'" + fieldSlug.replaceAll("'", "''") + "' ILIKE '%" + search.toLowerCase().replace("'", "''")
								+ "%' OR e.properties->>";
				}

			}
			query += " ) and e.layer_entity_element = '" + l.getId() + "'";
			List<UUID> uuids = jdbcTemplate.queryForList(query, UUID.class);

			Expression<UUID> exp = root.get("id");

			if (uuids != null)
				resultPre = exp.in(uuids);

			predicates.add(resultPre);

			Predicate finalPre = cb.and(predicates.toArray(new Predicate[] {}));

			TypedQuery<EntityElement> typedQuery = entityManager.createQuery(cq.select(root).where(finalPre));

			int total = typedQuery.getResultList().size();

			if (limit != -1)
				typedQuery.setFirstResult(page * limit).setMaxResults(limit);

			List<EntityElement> entityElements = typedQuery.getResultList();

			List<EntityElementSimpleDto> entityElementSimpleDtos = cModelMapper.mapList(entityElements,
					EntityElementSimpleDto.class);

			for (EntityElementSimpleDto entityElementSimpleDto : entityElementSimpleDtos) {

				Layer myLayer = layerRepository.findBySlug(layerSlug);

				Map<String, String> properties = new LinkedHashMap<>();

				if (myLayer != null) {
					List<Field> fields = myLayer.getFields().stream()
							.sorted(Comparator.comparingInt(Field::getOrder)).collect(Collectors.toList());

					fields.forEach(f -> {
						if(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken){
							if(f.getPublique() && f.getVisible()){
								properties.put(f.getSlug(), entityElementSimpleDto.getProperties().get(f.getSlug()));
							}
						}else if(f.getVisible()){
							properties.put(f.getSlug(), entityElementSimpleDto.getProperties().get(f.getSlug()));
						}
					});

					entityElementSimpleDto.setProperties(properties);
					if(entityElementSimpleDto.getGeom() != null){
						entityElementSimpleDto.setGeometryType(entityElementSimpleDto.getGeom().getGeometryType());
						entityElementSimpleDto.setCoordinates(entityElementSimpleDto.getGeom().getCoordinates());
					}
					entityElementSimpleDto.setGeom(null);
				}

			}

			pageDto.setContent(entityElementSimpleDtos);
			pageDto.setTotalElements(total);

		} catch (Exception e) {
			log.info(e.getMessage());
		}

		return pageDto;
	}
}
