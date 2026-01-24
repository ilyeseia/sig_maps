/**
 * 
 */
package dz.eadn.sig.service;

import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import dz.eadn.sig.dto.CommonFilter;
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.dto.UserNotificationDto;
import dz.eadn.sig.model.User;
import dz.eadn.sig.util.SearchCriteria;

/**
 * @author Achrouf Abdenour && LAMOUR AMEUR
 *
 */

public interface UserNotificationService {

	Predicate findByProperty(SearchCriteria criteria, CriteriaBuilder cb, Root<?> root);

	PageDto<UserNotificationDto> findAllNotifications(User user, Boolean viewed, Integer page, Integer limit,
			String sort, String dir);

	UserNotificationDto setViewed(UUID id);

	PageDto<UserNotificationDto> findAllNotificationsByFilter(CommonFilter filter, Boolean viewed, Integer page,
			Integer limit, String sort, String dir);

}
