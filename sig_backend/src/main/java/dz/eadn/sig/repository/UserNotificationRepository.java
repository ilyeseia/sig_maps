/**
 * 
 */
package dz.eadn.sig.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import dz.eadn.sig.model.User;
import dz.eadn.sig.model.UserNotification;
import dz.eadn.sig.repository.common.CommonRepository;

/**
 * @author Achrouf Abdenour && LAMOUR AMEUR
 *
 */

@Primary
public interface UserNotificationRepository extends CommonRepository<UserNotification> {

	Page<UserNotification> findByUserAndViewed(User user, Boolean viewed, Pageable pageable);

}
