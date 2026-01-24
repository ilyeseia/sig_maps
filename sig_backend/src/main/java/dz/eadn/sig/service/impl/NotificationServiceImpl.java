/**
 * 
 */
package dz.eadn.sig.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dz.eadn.sig.dto.NotificationDto;
import dz.eadn.sig.model.EntityElement;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.Notification;
import dz.eadn.sig.repository.NotificationRepository;
import dz.eadn.sig.service.NotificationService;
import dz.eadn.sig.service.common.impl.CommonServiceImpl;
import dz.eadn.sig.util.QueryPattern;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour
 *
 */

@Slf4j
@Service
public class NotificationServiceImpl extends CommonServiceImpl<Notification, NotificationDto>
		implements NotificationService {

	@Autowired
	private NotificationRepository notificationRepository;

	public NotificationServiceImpl() {
		super(Notification.class);
	}

	public Optional<Notification> findByLayer(Layer layer) {
		return notificationRepository.findByLayer(layer);
	}

	public String evaluateMessage(Notification notification, EntityElement element) {
		QueryPattern queryPattern = new QueryPattern(notification.getTemplate());
		String result = null;
		try {
			result = queryPattern.evaluate(element.getProperties());
		} catch (Throwable e) {
			log.error(e.getMessage());
		}

		return result;
	}
}
