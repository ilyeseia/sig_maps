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
import dz.eadn.sig.dto.SettingsDto;
import dz.eadn.sig.dto.SystemNotification;
import dz.eadn.sig.dto.Transaction;
import dz.eadn.sig.exceptions.EntityAlreadyExistsException;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.mapper.SettingsMapper;
import dz.eadn.sig.model.NotificationLevel;
import dz.eadn.sig.model.NotificationObject;
import dz.eadn.sig.model.Operation;
import dz.eadn.sig.model.Settings;
import dz.eadn.sig.model.SettingsType;
import dz.eadn.sig.model.User;
import dz.eadn.sig.repository.SettingsRepository;
import dz.eadn.sig.service.NotificationMessageService;
import dz.eadn.sig.service.SettingsService;
import dz.eadn.sig.service.SettingsTypeService;
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
public class SettingsServiceImpl extends CommonServiceImpl<Settings, SettingsDto> implements SettingsService {

	@Autowired
	private SettingsRepository settingsRepository;

	@Autowired
	private SettingsTypeService settingsTypeService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private SettingsMapper settingsMapper;

	@Autowired
	private NotificationMessagesDto messages;

	@Autowired
	private NotificationMessageService notificationMessageService;

	public SettingsServiceImpl() {
		super(Settings.class);
	}

	public Settings findByCode(String code) {
		return settingsRepository.findByCode(code);
	}

	@Override
	public SettingsDto save(SettingsDto settingsDto) {

		SettingsDto savedSettings = null;

		if (settingsDto != null) {

			Settings existsettings = settingsRepository.findByCode(settingsDto.getCode());

			if (existsettings != null && !settingsDto.getId().equals(existsettings.getId())) {
				throw new EntityAlreadyExistsException(
						String.format("settings avec le code <%s> est toujours exsite ", settingsDto.getCode()));
			}
		}

		Settings settings = settingsMapper.dtoToEntity(settingsDto);

		settings = settingsRepository.save(settings);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (settings != null) {
			savedSettings = settingsMapper.entityToDto(settings);
			if (settingsDto.getId() == null) {

				String message = String.format(messages.getMessages().get("NM_SETTINGS_CREATE"), settings.getCode(),
						authentication.getName());

				SystemNotification systemNotification = createSystemNotification(Transaction.ADD, savedSettings);

				NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.CREATION,
						message, systemNotification, new ArrayList<>());

				notificationMessageService.sendNotificationMessage(notification);

			} else {
				String message = String.format(messages.getMessages().get("NM_SETTINGS_UPDATE"), settings.getCode(),
						authentication.getName());

				SystemNotification systemNotification = createSystemNotification(Transaction.UPDATE, savedSettings);

				NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.MODIFICATION,
						message, systemNotification, new ArrayList<>());

				notificationMessageService.sendNotificationMessage(notification);
			}
		}

		return savedSettings;
	}

	@Override
	public void delete(UUID id) {

		Settings settings = findById(id);

		if (settings == null) {
			throw new EntityNotFoundException("Impossible de supprimer l'entit� " + domainClass.getSimpleName());
		}

		settingsRepository.delete(settings);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String message = String.format(messages.getMessages().get("NM_SETTINGS_DELETE"), settings.getCode(),
				authentication.getName());

		SystemNotification systemNotification = createSystemNotification(Transaction.DELETE,
				modelMapper.map(settings, SettingsDto.class));

		NotificationSimpleDto notification = createNotification(NotificationLevel.SEVERE, Operation.SUPPRISSION,
				message, systemNotification, new ArrayList<>());

		notificationMessageService.sendNotificationMessage(notification);
	}

	@Override
	public List<Settings> findByTypeAndEnabled(String type, boolean enabled) {
		SettingsType settingsType = settingsTypeService.findByCode(type);
		return settingsRepository.findByTypeAndEnabled(settingsType, enabled);
	}

	@Override
	public List<Settings> getStatisticsSetting() {
		return settingsRepository.findByTypeAndEnabled(settingsTypeService.findByCode("STATISTIQUES"), true);
	}

	public NotificationSimpleDto createNotification(NotificationLevel level, Operation operation, String message,
			SystemNotification systemNotification, List<User> users) {

		NotificationSimpleDto notification = new NotificationSimpleDto();

		notification.setObject(NotificationObject.Paramettre);
		notification.setLevel(level);
		notification.setOperation(operation);
		notification.setDestination("settings");
		notification.setMessage(message);
		notification.setSystemNotification(systemNotification);
		notification.setUsers(users);

		return notification;
	}

	public SystemNotification createSystemNotification(Transaction transaction, Object object) {
		SystemNotification systemNotification = new SystemNotification();

		systemNotification.setType("settings");
		systemNotification.setTransaction(transaction);
		systemNotification.setObject(object);

		return systemNotification;
	}
}
