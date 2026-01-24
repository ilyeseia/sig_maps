/**
 * 
 */
package dz.eadn.sig.service;

import java.util.Optional;

import dz.eadn.sig.dto.NotificationDto;
import dz.eadn.sig.model.EntityElement;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.Notification;
import dz.eadn.sig.service.common.CommonService;

/**
 * @author Achrouf Abdenour
 *
 */
public interface NotificationService extends CommonService<Notification, NotificationDto> {
	Optional<Notification> findByLayer(Layer layer);

	String evaluateMessage(Notification notification, EntityElement element);
}
