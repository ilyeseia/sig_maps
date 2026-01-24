/**
 * 
 */
package dz.eadn.sig.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.NotificationDto;
import dz.eadn.sig.model.Notification;
import dz.eadn.sig.service.NotificationService;

/**
 * @author Achrouf Abdenour
 *
 */

@Component
public class NotificationMapper extends CommonMapper<Notification, NotificationDto> {

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private LayerMapper layerMapper;

	@Override
	protected NotificationDto mapEntityToDto(Notification notification) {
		NotificationDto dto = new NotificationDto();
		dto.setLayerDto(layerMapper.entityToDto(notification.getLayer()));
		dto.setTemplate(notification.getTemplate());
		return dto;
	}

	@Override
	protected Notification mapDtoToEntity(NotificationDto dto) {
		Notification notification = notificationService.findById(dto.getId(), true);

		if (notification == null) {
			if (dto.getId() != null) {
				return null;
			}
			notification = new Notification();
		} else {
			if (notification.getDeleted())
				throw new RuntimeException("can't do operation on deleted map");
		}

		if (dto.getLayerDto() != null) {
			notification.setLayer(layerMapper.dtoToEntity(dto.getLayerDto()));
		}

		if (dto.getTemplate() != null) {
			notification.setTemplate(dto.getTemplate());
		}

		return notification;
	}

}
