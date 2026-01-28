package dz.eadn.sig.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import dz.eadn.sig.model.NotificationLevel;
import dz.eadn.sig.model.NotificationObject;
import dz.eadn.sig.model.Operation;
import dz.eadn.sig.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LAMOUR AMEUR
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificationSimpleDto {

	@Enumerated(EnumType.STRING)
	private NotificationObject Object;

	@Enumerated(EnumType.STRING)
	private NotificationLevel level;

	@Enumerated(EnumType.STRING)
	private Operation operation;

	private String destination = "";
	private String message = "";

	private SystemNotification systemNotification;
	private List<User> users = new ArrayList<>();

}
