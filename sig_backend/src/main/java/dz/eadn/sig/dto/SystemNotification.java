/**
 * 
 */
package dz.eadn.sig.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Achrouf Abdenour && AMEUR LAMOUR && LOKBANI Chouaib
 *
 */

@Getter @Setter
public class SystemNotification {

	@Enumerated(EnumType.STRING)
	private Transaction transaction;
	private String type;
	private Object object;
	private UserNotificationDto userNotificationDto;
}
