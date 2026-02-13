package dz.eadn.sig.service.common.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

import dz.eadn.sig.model.Layer;
import org.locationtech.jts.geom.Geometry;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import dz.eadn.sig.dto.CommonFilter;
import dz.eadn.sig.dto.LayerDto;
import dz.eadn.sig.dto.MapDto;
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.dto.SessionDto;
import dz.eadn.sig.dto.SystemNotification;
import dz.eadn.sig.dto.Transaction;
import dz.eadn.sig.dto.UserDto;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.exceptions.GlobalException;
import dz.eadn.sig.mapper.CommonMapper;
import dz.eadn.sig.model.Field;
import dz.eadn.sig.repository.UserNotificationRepository;
import dz.eadn.sig.repository.common.CommonRepository;
import dz.eadn.sig.security.RedisUtil;
import dz.eadn.sig.service.FieldService;
import dz.eadn.sig.service.common.CommonService;
import dz.eadn.sig.util.SearchCriteria;
import dz.eadn.sig.util.WITHUUID;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrosuf Abdenour & Ameur LAMOUR
 *
 * 
 */
@Slf4j
public abstract class CommonServiceImpl<CommonObject extends WITHUUID, CommonDto extends WITHUUID>
		implements CommonService<CommonObject, CommonDto> {

	@Autowired
	private CommonRepository<CommonObject> repository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	protected Class<CommonObject> domainClass;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	UserNotificationRepository userNotificationRepository;

	@Autowired
	private FieldService fieldService;

	@Autowired
	protected CommonMapper<CommonObject, CommonDto> mapper;

	@Autowired
	public CommonServiceImpl(Class<CommonObject> domainClass) {
		this.domainClass = domainClass;
	}

	@Override
	public long count() {
		return repository.count();
	}

	@Override
	public boolean existsById(UUID id) {
		return repository.existsById(id);
	}

	@Override
	public CommonObject findById(UUID id) {
		CommonObject commonObject = findById(id, false);
		if (commonObject == null)
			throw new EntityNotFoundException(String.format("<%s> non trouv� ", domainClass));

		return commonObject;
	}

	@Override
	public CommonObject findById(UUID id, boolean getDeleted) {
		if (id == null) {
			return null;
		}

		Optional<CommonObject> oco = repository.findById(id);

		if (!oco.isPresent())
//			return null;
			throw new EntityNotFoundException(String.format("<%s> non trouv� ", domainClass));

		CommonObject commonObject = oco.get();

		if (getDeleted)
			return commonObject;
		else
			return commonObject.getDeleted() ? null : commonObject;
	}

	@Override
	public List<CommonObject> findAll(Integer page, Integer limit, String sort, String dir, boolean getDeleted) {

		Pageable paging = null;

		if (dir.equals("asc"))
			paging = PageRequest.of(page, limit, Sort.by(sort).ascending());
		else
			paging = PageRequest.of(page, limit, Sort.by(sort).descending());

		Page<CommonObject> pagedResult = repository.findAll(paging);

		List<CommonObject> instances;
		if (pagedResult.hasContent()) {
			instances = pagedResult.getContent();
		} else {
			return new ArrayList<CommonObject>();
		}

		List<CommonObject> result = new ArrayList<CommonObject>();
		for (CommonObject co : instances) {
			if (getDeleted) {
				result.add(co);
			} else {
				if (co.getDeleted().equals(false)) {
					result.add(co);
				}
			}
		}
		return result;
	}

	@Override
	public List<CommonObject> findAll(Integer page, Integer limit, String sort, String dir) {
		return findAll(page, limit, sort, dir, false);
	}

	@Override
	public CommonDto save(CommonDto commonDto) {

		CommonObject commonObject = mapper.dtoToEntity(commonDto);

		commonObject = repository.save(commonObject);

		return mapper.entityToDto(commonObject);

	}

	@Override
	public List<CommonObject> saveAll(Collection<CommonObject> instances) throws Exception {
		return repository.saveAll(instances);
	}

	@Override
	public void delete(UUID id) {

		CommonObject result = findById(id);

		if (result == null) {
			throw new EntityNotFoundException("Impossible de supprimer l'entit� " + domainClass.getSimpleName());
		}

		result.setDeleted(true);

		repository.save(result);

		sendNotification(id, Transaction.DELETE, domainClass.getSimpleName());

	}

	@Override
	public void deleteAll(Collection<CommonObject> instances) {
		for (CommonObject instance : instances) {
			instance.setDeleted(true);
			repository.save(instance);
		}
	}

	@Override
	public Predicate findByProperty(SearchCriteria criteria, CriteriaBuilder cb, Root<?> root) {

		Predicate predicate = null;

		try {

			if (criteria.getType().equals("date")) {
				if(criteria.getOperator().equals("<>")){
					try {
						predicate = cb.between(root.get(criteria.getField().replaceAll("'", "''")),
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(criteria.getValue().split("_")[0]),
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(criteria.getValue().split("_")[1]));
					} catch (ParseException e) {
						log.info(e.getMessage());
					}
				}
				else if (criteria.getOperator().equals(">=")) {
					try {
						predicate = cb.greaterThanOrEqualTo(root.get(criteria.getField().replaceAll("'", "''")),
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(criteria.getValue()));
					} catch (ParseException e) {
						log.info(e.getMessage());
					}
				} else if (criteria.getOperator().equals("<=")) {
					try {
						predicate = cb.lessThanOrEqualTo(root.get(criteria.getField().replaceAll("'", "''")),
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(criteria.getValue()));
					} catch (ParseException e) {
						log.info(e.getMessage());
					}
				} else if (criteria.getOperator().equals("<")) {
					try {
						predicate = cb.lessThan(root.get(criteria.getField().replaceAll("'", "''")),
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(criteria.getValue()));
					} catch (ParseException e) {
						log.info(e.getMessage());
					}
				} else if (criteria.getOperator().equals(">")) {
					try {
						predicate = cb.greaterThan(root.get(criteria.getField().replaceAll("'", "''")),
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(criteria.getValue()));
					} catch (ParseException e) {
						log.info(e.getMessage());
					}
				}

			} else {
				if (criteria.getOperator().equals(">=")) {
					predicate = cb.greaterThanOrEqualTo(root.get(criteria.getLabel()), criteria.getValue().toString());
				} else if (criteria.getOperator().equals("<=")) {
					predicate = cb.lessThanOrEqualTo(root.get(criteria.getLabel()), criteria.getValue().toString());
				} else if (criteria.getOperator().equals("ilike")) {
					if (root.get(criteria.getLabel()).getJavaType().equals(String.class)) {
						predicate = cb.like(cb.lower(root.get(criteria.getLabel())),
								"%" + criteria.getValue().toLowerCase().replace("'", "''") + "%");
					} else {
						predicate = cb.equal(root.get(criteria.getLabel()), criteria.getValue().replace("'", "''"));
					}
				}

			}
		} catch (Exception e) {
			log.info(e.getMessage());
			throw new GlobalException(e.getMessage());
		}

		return predicate;

	}

	@Override
	public boolean isUUID(String string) {
		try {
			UUID.fromString(string);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public Predicate findBySpatial(SearchCriteria criteria, Root<?> root, Geometry geometry) {

		Predicate predicate = null;

		String query = "SELECT e FROM EntityElement e WHERE " + criteria.getOperator() + "(:region, e.geom) = true";

		List<CommonObject> commonObjects = entityManager.createQuery(query).setParameter("region", geometry)
				.getResultList();

		List<UUID> uuids = commonObjects.stream().map(ee -> ee.getId()).collect(Collectors.toList());

		Expression<UUID> exp = root.get("id");

		if (uuids != null && uuids.size() != 0)
			predicate = exp.in(uuids);

		return predicate;
	}

	@Override
	public Predicate findByField(SearchCriteria criteria, Root<?> root, Layer layer) {

		Predicate predicate = null;
		String query = "";

		try {

			Optional<Field> field = fieldService.findFieldBySlugAndLayer(criteria.getField(), layer);

			if (field != null) {
				query = buildQuery(field.get().getType().toString(), criteria, true);
				query += " and e.layer_entity_element = '" + layer.getId() + "'";
				List<UUID> uuids = jdbcTemplate.queryForList(query, UUID.class);

				Expression<UUID> exp = root.get("id");

				if (uuids != null && uuids.size() != 0)
					predicate = exp.in(uuids);
				else
					predicate = exp.in(UUID.randomUUID());
			}
		} catch (Exception e) {
			log.info(e.getMessage());
			throw new GlobalException(e.getMessage());
		}
		return predicate;
	}

	@Override
	public String buildQuery(String fieldType, SearchCriteria criteria, boolean fullQuery) {
		String query = fullQuery ? "SELECT e.id FROM sig.entity_element e WHERE" : "";
		switch (fieldType) {
			case "SELECT": {
				if (criteria.getOperator().equals("ilike")) {
					criteria.setValue("%" + criteria.getValue() + "%");
				}
				query += "  LOWER(split_part(e.properties ->> '" + criteria.getField().replaceAll("'", "''")
						+ "', ':', 2 ))" + criteria.getOperator() + " LOWER('" + criteria.getValue().replace("'", "''") + "') ";
				break;
			}
			case "BOOLEAN":
			case "TEXT":
				{
				if (criteria.getOperator().equals("ilike")) {
					criteria.setValue("%" + criteria.getValue() + "%");
				}
				query += "  LOWER(e.properties ->> '" + criteria.getField().replaceAll("'", "''")
						+ "') " + criteria.getOperator() + " LOWER('" + criteria.getValue().replace("'", "''") + "') ";
				break;
			}
			case "NUMBER": {
				if(!criteria.getOperator().equals("ilike")){
					query += " (e.properties->>'" + criteria.getField().replaceAll("'", "''")
							+ "' != '') and (e.properties ->> '" + criteria.getField().replaceAll("'", "''") + "') :: float "
							+ criteria.getOperator() + " " + criteria.getValue() + " and e.properties ->> '" + criteria.getField().replaceAll("'", "''") + "' ~ '^[0-9\\.]+$'";
				}else{
					query += " (e.properties->>'" + criteria.getField().replaceAll("'", "''")
							+ "' != '') and (e.properties ->> '" + criteria.getField().replaceAll("'", "''") + "')  "
							+ criteria.getOperator() + " '" + criteria.getValue() + "'";
				}
				break;
			}
			case "INTEGER": {
				query += " (e.properties->>'" + criteria.getField().replaceAll("'", "''")
						+ "' != '') and (e.properties ->> '" + criteria.getField().replaceAll("'", "''") + "') :: float "
						+ criteria.getOperator() + " " + criteria.getValue() + " and e.properties ->> '" + criteria.getField().replaceAll("'", "''") + "' ~ '^[0-9\\.]+$'";
				break;
			}
			case "DATETIME": {
				if(criteria.getOperator().equals("between")){
					query += " e.properties->>'" + criteria.getField().replaceAll("'", "''")
							+ "' != '' and TO_TIMESTAMP(e.properties ->> '" + criteria.getField().replaceAll("'", "''")
							+ "', 'DD/MM/YYYY HH24:MI:SS') " + criteria.getOperator() + criteria.getValue();
				}else{
					query += " e.properties->>'" + criteria.getField().replaceAll("'", "''")
							+ "' != '' and TO_TIMESTAMP(e.properties ->> '" + criteria.getField().replaceAll("'", "''")
							+ "', 'DD/MM/YYYY HH24:MI:SS') " + criteria.getOperator() + " '" + criteria.getValue() + "'";
				}
				break;
			}
			case "DATE": {
				if(criteria.getOperator().equals("between")){
					query += " e.properties->>'" + criteria.getField().replaceAll("'", "''")
							+ "' != '' and TO_TIMESTAMP(e.properties ->> '" + criteria.getField().replaceAll("'", "''")
							+ "','MM/DD/YYYY') " + criteria.getOperator() + criteria.getValue();
				}else{
					query += " e.properties->>'" + criteria.getField().replaceAll("'", "''")
							+ "' != '' and TO_TIMESTAMP(e.properties ->> '" + criteria.getField().replaceAll("'", "''")
							+ "','MM/DD/YYYY') " + criteria.getOperator() + " '" + criteria.getValue() + "'";
				}
				break;
			}
			case "TIME": {
				query += " e.properties->>'" + criteria.getField().replaceAll("'", "''")
						+ "' != '' and TO_TIMESTAMP(e.properties ->> '" + criteria.getField().replaceAll("'", "''")
						+ "','HH24:MI:SS') :: TIME " + criteria.getOperator() + " '" + criteria.getValue() + "'";
				break;
			}
			default:
				break;
		}
		return query;
	}

	@Override
	public List<CommonObject> findByAdvancedFilter(CommonFilter filter, Integer page, Integer limit, String sort,
			String dir) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CommonObject> cq = cb.createQuery(domainClass);
		Root<CommonObject> root = cq.from(domainClass);

		List<Predicate> predicates = new ArrayList<>();

		if (dir.equals("asc"))
			cq.orderBy(cb.asc(root.get(sort)));
		else
			cq.orderBy(cb.desc(root.get(sort)));

		predicates.add(cb.equal(root.get("deleted"), false));

		Predicate finalPredicate = null;

		for (SearchCriteria criteria : filter.getRules()) {

			predicates.add(findByProperty(criteria, cb, root));

		}

		if (filter.getCondition().equals("or"))
			finalPredicate = cb.or(predicates.toArray(new Predicate[] {}));
		else
			finalPredicate = cb.and(predicates.toArray(new Predicate[] {}));

		TypedQuery<CommonObject> typedQuery = entityManager.createQuery(cq.select(root).where(finalPredicate));

		if (limit != -1)
			typedQuery.setFirstResult(page * limit).setMaxResults(limit);

		return typedQuery.getResultList();
	}

	@Override
	public PageDto<CommonDto> findAllByFilter(CommonFilter filter, Integer page, Integer limit, String sort,
			String dir) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CommonObject> cq = cb.createQuery(domainClass);
		Root<CommonObject> root = cq.from(domainClass);

		List<Predicate> predicates = new ArrayList<>();
		PageDto<CommonDto> pageDto = new PageDto<>();

		Predicate deletePre, commonsPre, finalPre = null;

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

		TypedQuery<CommonObject> typedQuery = entityManager.createQuery(cq.select(root).where(finalPre));
		int total = typedQuery.getResultList().size();

		if (limit != -1)
			typedQuery.setFirstResult(page * limit).setMaxResults(limit);

		List<CommonObject> commonObjects = typedQuery.getResultList();

		pageDto.setContent(mapper.entitysToDtos(commonObjects));
		pageDto.setTotalElements(total);

		return pageDto;

	}

	@Override
	public PageDto<CommonDto> findAllByPage(Integer page, Integer limit, String sort, String dir) {

		PageDto<CommonDto> pagesDto = new PageDto<>();

		if (limit == -1)
			limit = repository.findAll().size();

		if (limit > 0) {
			PageRequest pageable = PageRequest.of(page, limit, Direction.fromString(dir), sort);

			Page<CommonObject> enPage = repository.findAll(pageable);

			List<CommonDto> commonDtos = mapper.entitysToDtos(enPage.getContent());

			pagesDto.setContent(commonDtos);
			pagesDto.setTotalElements(enPage.getTotalElements());

			return pagesDto;
		} else
			return pagesDto;
	}

	public void sendNotification(Object object, Transaction tr, String type) {
		SystemNotification notification = new SystemNotification();
		notification.setTransaction(tr);
		notification.setObject(object);
		notification.setType(type.toLowerCase());

		sendNotification(notification);
	}

	public void sendNotification(SystemNotification notification) {
		switch (notification.getType()) {
		case "Layer":
			LayerDto layer = (LayerDto) notification.getObject();
			sendNotificationToUsers(layer.getUserDtos(), notification);
			break;
		case "Map":
			MapDto map = (MapDto) notification.getObject();
			sendNotificationToUsers(map.getUserDtos(), notification);
			break;
		default:
			sendNotificationToAll(notification);
			break;
		}
	}

	public void sendNotificationToAll(SystemNotification notification) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		for (SessionDto session : redisUtil.findAllSessions()) {
			if (!session.getUserName().equals(authentication.getName())) {
				String destination = "/notification/" + notification.getType();
				messagingTemplate.convertAndSendToUser(session.getToken(), destination, notification);
			}
		}
	}

	public void sendNotificationToUsers(List<UserDto> users, SystemNotification notification) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Set<String> usersSet = users.stream().map(user -> user.getUsername()).collect(Collectors.toSet());
		for (SessionDto session : redisUtil.findAllSessions()) {
			if (!session.getUserName().equals(authentication.getName()) && usersSet.contains(session.getUserName())) {
				String destination = "/notification/" + notification.getType();
				messagingTemplate.convertAndSendToUser(session.getToken(), destination, notification);
			}
		}
	}

}
