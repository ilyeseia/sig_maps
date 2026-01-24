/**
 * 
 */
package dz.eadn.sig.repository;

import java.util.Optional;

import org.springframework.context.annotation.Primary;

import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.Notification;
import dz.eadn.sig.repository.common.CommonRepository;

/**
 * @author Achrouf Abdenour
 *
 */
public interface NotificationRepository extends CommonRepository<Notification> {
	Optional<Notification> findByLayer(Layer layer);
}
