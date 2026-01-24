/**
 * 
 */
package dz.eadn.sig.dto;

import java.util.Date;
import java.util.UUID;

import dz.eadn.sig.model.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Achrouf Abdenour && LAMOUR AMEUR
 *
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserNotificationDto {

	private UUID id;

	private String message;

	private String object;

	private String level;

	private Operation operation;

	private String icon;

	private String link;

	private Boolean viewed;

	private Date viewedDate;

	private String createdBy;

	private Date createDate;

}
