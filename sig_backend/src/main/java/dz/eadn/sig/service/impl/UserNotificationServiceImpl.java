/**
 * 
 */
package dz.eadn.sig.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import dz.eadn.sig.dto.CommonFilter;
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.dto.UserNotificationDto;
import dz.eadn.sig.exceptions.GlobalException;
import dz.eadn.sig.mapper.CommonModelMapper;
import dz.eadn.sig.model.User;
import dz.eadn.sig.model.UserNotification;
import dz.eadn.sig.repository.UserNotificationRepository;
import dz.eadn.sig.service.UserNotificationService;
import dz.eadn.sig.service.UserService;
import dz.eadn.sig.util.SearchCriteria;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour && LAMOUR AMEUR
 *
 */
@Service
@Slf4j
public class UserNotificationServiceImpl implements UserNotificationService {

	@Autowired
	private UserNotificationRepository userNotificationRepository;

	@Autowired
	private CommonModelMapper<?, ?> cModelMapper;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private UserService userService;

	@Override
	public PageDto<UserNotificationDto> findAllNotifications(User user, Boolean viewed, Integer page, Integer limit,
			String sort, String dir) {
		PageDto<UserNotificationDto> pageDto = new PageDto<>();
		Sort sortDir = null;

		if (user != null) {

			if (dir.equals("asc"))
				sortDir = Sort.by(sort).ascending();
			else
				sortDir = Sort.by(sort).descending();

			Pageable pageable = PageRequest.of(page, limit, sortDir);

			Page<UserNotification> userNotifications = userNotificationRepository.findByUserAndViewed(user, viewed,
					pageable);

			List<UserNotificationDto> userNotificationDtos = cModelMapper.mapList(userNotifications.getContent(),
					UserNotificationDto.class);

			pageDto.setContent(userNotificationDtos);
			pageDto.setTotalElements(userNotifications.getTotalElements());

		}

		return pageDto;
	}

	@Override
	public UserNotificationDto setViewed(UUID id) {
		Optional<UserNotification> optional = userNotificationRepository.findById(id);
		UserNotification userNotification = null;
		if (optional.isPresent()) {
			userNotification = optional.get();
			Boolean viewed = userNotification.getViewed() != null ? !userNotification.getViewed()
					: userNotification.getViewed();
			userNotification.setViewed(viewed);
			userNotification.setViewedDate(new Date());
			userNotificationRepository.save(userNotification);
		}

		return modelMapper.map(userNotification, UserNotificationDto.class);
	}

	@Override
	public Predicate findByProperty(SearchCriteria criteria, CriteriaBuilder cb, Root<?> root) {

		Predicate predicate = null;

		try {

			if (criteria.getType().equals("date")) {
				if (criteria.getOperator().equals(">=")) {
					try {
						predicate = cb.greaterThanOrEqualTo(root.get(criteria.getField()),
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(criteria.getValue()));
					} catch (ParseException e) {
						log.info(e.getMessage());
					}
				} else if (criteria.getOperator().equals("<=")) {
					try {
						predicate = cb.lessThanOrEqualTo(root.get(criteria.getField()),
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(criteria.getValue()));
					} catch (ParseException e) {
						log.info(e.getMessage());
					}
				} else if (criteria.getOperator().equals("<")) {
					try {
						predicate = cb.lessThan(root.get(criteria.getField()),
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(criteria.getValue()));
					} catch (ParseException e) {
						log.info(e.getMessage());
					}
				} else if (criteria.getOperator().equals(">")) {
					try {
						predicate = cb.greaterThan(root.get(criteria.getField()),
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
	public PageDto<UserNotificationDto> findAllNotificationsByFilter(CommonFilter filter, Boolean viewed, Integer page,
			Integer limit, String sort, String dir) {

		List<Predicate> predicates = new ArrayList<>();
		PageDto<UserNotificationDto> pageDto = new PageDto<>();

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserNotification> cq = cb.createQuery(UserNotification.class);
		Root<UserNotification> root = cq.from(UserNotification.class);

		Join<UserNotification, User> UserNotificationjoinUser = root.join("user", JoinType.LEFT);

		Predicate deletePre, commonsPre, ownerPre, finalPre, viewedPre = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		User user = userService.findByUsername(username);

		ownerPre = cb.or(cb.equal(UserNotificationjoinUser.get("id"), user.getId()));

		if (dir.equals("asc"))
			cq.orderBy(cb.asc(root.get(sort)));
		else
			cq.orderBy(cb.desc(root.get(sort)));

		deletePre = cb.equal(root.get("deleted"), false);

		viewedPre = cb.equal(root.get("viewed"), viewed);

		for (SearchCriteria criteria : filter.getRules()) {
			if (criteria.getValue() != "")
				predicates.add(findByProperty(criteria, cb, root));
		}

		if (filter.getCondition().equals("or"))
			commonsPre = cb.or(predicates.toArray(new Predicate[] {}));
		else
			commonsPre = cb.and(predicates.toArray(new Predicate[] {}));

		finalPre = cb.and(commonsPre, deletePre, viewedPre, ownerPre);

		TypedQuery<UserNotification> typedQuery = entityManager.createQuery(cq.select(root).where(finalPre));

		int total = typedQuery.getResultList().size();

		if (limit != -1)
			typedQuery.setFirstResult(page * limit).setMaxResults(limit);

		List<UserNotification> userNotifications = typedQuery.getResultList();

		pageDto.setContent(cModelMapper.mapList(userNotifications, UserNotificationDto.class));
		pageDto.setTotalElements(total);

		return pageDto;
	}
}
