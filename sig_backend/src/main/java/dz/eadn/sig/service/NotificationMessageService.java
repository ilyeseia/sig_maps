package dz.eadn.sig.service;

import dz.eadn.sig.dto.NotificationSimpleDto;

public interface NotificationMessageService {

	void sendNotificationMessage(NotificationSimpleDto notification);

}
