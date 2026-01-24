package dz.eadn.sig.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import dz.eadn.sig.dto.NotificationMessagesDto;
import dz.eadn.sig.dto.NotificationSimpleDto;
import dz.eadn.sig.dto.SettingsTypeDto;
import dz.eadn.sig.dto.SystemNotification;
import dz.eadn.sig.dto.Transaction;
import dz.eadn.sig.exceptions.EntityAlreadyExistsException;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.model.NotificationLevel;
import dz.eadn.sig.model.NotificationObject;
import dz.eadn.sig.model.Operation;
import dz.eadn.sig.model.SettingsType;
import dz.eadn.sig.model.User;
import dz.eadn.sig.repository.SettingsTypeRepository;
import dz.eadn.sig.service.NotificationMessageService;
import dz.eadn.sig.service.SettingsTypeService;
import dz.eadn.sig.service.common.impl.CommonServiceImpl;

/**
 * @author Ameur LAMOUR
 *
 */
@Service
public class SettingsTypeServiceImpl extends CommonServiceImpl<SettingsType, SettingsTypeDto>
		implements SettingsTypeService {

	@Autowired
	private SettingsTypeRepository settingsTypeRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private NotificationMessagesDto messages;

	@Autowired
	private NotificationMessageService notificationMessageService;

	public SettingsTypeServiceImpl() {
		super(SettingsType.class);
	}

	public SettingsType findByCode(String code) {
		return settingsTypeRepository.findByCode(code);
	}

	@Override
	public SettingsTypeDto save(SettingsTypeDto settingsTypeDto) {

		SettingsTypeDto savedSettingsType = null;
		if (settingsTypeDto != null) {

			SettingsType existTypeParametre = settingsTypeRepository.findByCode(settingsTypeDto.getCode());

			if (existTypeParametre != null && !settingsTypeDto.getId().equals(existTypeParametre.getId())) {
				throw new EntityAlreadyExistsException(String
						.format("Type Parametre avec le code <%s> est toujours exsite ", settingsTypeDto.getCode()));
			}
		}

		SettingsType settingsType = modelMapper.map(settingsTypeDto, SettingsType.class);

		settingsType = settingsTypeRepository.save(settingsType);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (settingsType != null) {
			savedSettingsType = modelMapper.map(settingsType, SettingsTypeDto.class);
			if (settingsTypeDto.getId() == null) {

				String message = String.format(messages.getMessages().get("NM_SETTINGS_TYPE_CREATE"),
						savedSettingsType.getCode(), authentication.getName());

				SystemNotification systemNotification = createSystemNotification(Transaction.ADD, savedSettingsType);

				NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.CREATION,
						message, systemNotification, new ArrayList<>());

				notificationMessageService.sendNotificationMessage(notification);

			} else {
				String message = String.format(messages.getMessages().get("NM_SETTINGS_TYPE_UPDATE"),
						savedSettingsType.getCode(), authentication.getName());

				SystemNotification systemNotification = createSystemNotification(Transaction.UPDATE, savedSettingsType);

				NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.MODIFICATION,
						message, systemNotification, new ArrayList<>());

				notificationMessageService.sendNotificationMessage(notification);
			}
		}
		return savedSettingsType;

	}

	@Override
	public void delete(UUID id) {

		SettingsType settingsType = findById(id);

		if (settingsType == null) {
			throw new EntityNotFoundException("Impossible de supprimer l'entit� " + domainClass.getSimpleName());
		}

		settingsTypeRepository.delete(settingsType);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String message = String.format(messages.getMessages().get("NM_SETTINGS_TYPE_DELETE"), settingsType.getCode(),
				authentication.getName());

		SystemNotification systemNotification = createSystemNotification(Transaction.DELETE,
				modelMapper.map(settingsType, SettingsTypeDto.class));

		NotificationSimpleDto notification = createNotification(NotificationLevel.SEVERE, Operation.SUPPRISSION,
				message, systemNotification, new ArrayList<>());

		notificationMessageService.sendNotificationMessage(notification);

	}

	public NotificationSimpleDto createNotification(NotificationLevel level, Operation operation, String message,
			SystemNotification systemNotification, List<User> users) {

		NotificationSimpleDto notification = new NotificationSimpleDto();

		notification.setObject(NotificationObject.Type_paramettre);
		notification.setLevel(level);
		notification.setOperation(operation);
		notification.setDestination("settings_type");
		notification.setMessage(message);
		notification.setSystemNotification(systemNotification);
		notification.setUsers(users);

		return notification;
	}

	public SystemNotification createSystemNotification(Transaction transaction, Object object) {
		SystemNotification systemNotification = new SystemNotification();

		systemNotification.setType("settingsType");
		systemNotification.setTransaction(transaction);
		systemNotification.setObject(object);

		return systemNotification;
	}
}
