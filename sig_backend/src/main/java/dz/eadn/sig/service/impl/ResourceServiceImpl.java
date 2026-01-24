package dz.eadn.sig.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dz.eadn.sig.exceptions.GlobalException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType;

import dz.eadn.sig.dto.NotificationMessagesDto;
import dz.eadn.sig.dto.NotificationSimpleDto;
import dz.eadn.sig.dto.ResourceDto;
import dz.eadn.sig.dto.SystemNotification;
import dz.eadn.sig.dto.Transaction;
import dz.eadn.sig.exceptions.EntityAlreadyExistsException;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.mapper.ResourceMapper;
import dz.eadn.sig.model.Field;
import dz.eadn.sig.model.FieldType;
import dz.eadn.sig.model.NotificationLevel;
import dz.eadn.sig.model.NotificationObject;
import dz.eadn.sig.model.Operation;
import dz.eadn.sig.model.Resource;
import dz.eadn.sig.model.User;
import dz.eadn.sig.repository.ResourceRepository;
import dz.eadn.sig.service.NotificationMessageService;
import dz.eadn.sig.service.ResourceService;
import dz.eadn.sig.service.common.impl.CommonServiceImpl;

/**
 * @author Achrouf Abdenour
 *
 */

/**
 * @author Ameur LAMOUR
 *
 */
@Service
public class ResourceServiceImpl extends CommonServiceImpl<Resource, ResourceDto> implements ResourceService {

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private ResourceMapper resourceMapper;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private NotificationMessagesDto messages;

	@Autowired
	private NotificationMessageService notificationMessageService;

	public ResourceServiceImpl() {
		super(Resource.class);
	}

	@Override
	public void delete(UUID id) {
		Resource resource = findById(id);

		if (resource == null) {
			throw new EntityNotFoundException("Impossible de supprimer l'entit� " + domainClass.getSimpleName());
		}

		for (Field field : resource.getFields()) {
			field.setResource(null);
		}

		if(resourceRepository.countAllByParentResource_Id(resource.getId()) > 0){
			throw new GlobalException("Ce référentiel a déjà des enfants, supprimez-les d'abord");
		}

		resourceRepository.delete(resource);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String message = String.format(messages.getMessages().get("NM_SETTINGS_TYPE_DELETE"), resource.getName(),
				authentication.getName());

		SystemNotification systemNotification = createSystemNotification(Transaction.DELETE,
				modelMapper.map(resource, ResourceDto.class));

		NotificationSimpleDto notification = createNotification(NotificationLevel.SEVERE, Operation.SUPPRISSION,
				message, systemNotification, new ArrayList<>());

		notificationMessageService.sendNotificationMessage(notification);
	}

	// automatic field matching ,not use for now
	public FieldType getFieldType(ColumnType columnType) {
		switch (columnType) {
		case STRING:
			return FieldType.TEXT;
		case NUMBER:
			return FieldType.NUMBER;
		case BOOLEAN:
			return FieldType.SELECT;
		case ARRAY:
			return FieldType.MULTI_SELECT;
		default:
			return FieldType.TEXT;
		}
	}

	@Override
	public ResourceDto save(ResourceDto resourceDto) {

		ResourceDto savedResource = null;

		if (resourceDto != null) {

			Resource existresource = resourceRepository.findByName(resourceDto.getName());

			if (existresource != null) {
				throw new EntityAlreadyExistsException(
						String.format("resource avec le nom <%s> est toujours exsite ", resourceDto.getName()));
			}

		}

		Resource resource = resourceMapper.dtoToEntity(resourceDto);

		resource = resourceRepository.save(resource);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (resource != null) {
			savedResource = resourceMapper.entityToDto(resource);
			if (resourceDto.getId() == null) {

				String message = String.format(messages.getMessages().get("NM_RESOURCE_CREATE"),
						savedResource.getName(), authentication.getName());

				SystemNotification systemNotification = createSystemNotification(Transaction.ADD, savedResource);

				NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.CREATION,
						message, systemNotification, new ArrayList<>());

				notificationMessageService.sendNotificationMessage(notification);

			} else {
				String message = String.format(messages.getMessages().get("NM_RESOURCE_UPDATE"),
						savedResource.getName(), authentication.getName());

				SystemNotification systemNotification = createSystemNotification(Transaction.UPDATE, savedResource);

				NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.MODIFICATION,
						message, systemNotification, new ArrayList<>());

				notificationMessageService.sendNotificationMessage(notification);
			}
		}

		return savedResource;
	}

	public NotificationSimpleDto createNotification(NotificationLevel level, Operation operation, String message,
			SystemNotification systemNotification, List<User> users) {

		NotificationSimpleDto notification = new NotificationSimpleDto();

		notification.setObject(NotificationObject.Ressource);
		notification.setLevel(level);
		notification.setOperation(operation);
		notification.setDestination("resource");
		notification.setMessage(message);
		notification.setSystemNotification(systemNotification);
		notification.setUsers(users);

		return notification;
	}

	public SystemNotification createSystemNotification(Transaction transaction, Object object) {
		SystemNotification systemNotification = new SystemNotification();

		systemNotification.setType("resources");
		systemNotification.setTransaction(transaction);
		systemNotification.setObject(object);

		return systemNotification;
	}

	@Override
	public List<ResourceDto> getAllResourceChildren(UUID resourceID) {
		return resourceMapper.entitysToDtos(resourceRepository.findResourceByParentResource_Id(resourceID));
	}
}
