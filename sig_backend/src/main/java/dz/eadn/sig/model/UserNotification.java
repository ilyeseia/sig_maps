/**
 * 
 */
package dz.eadn.sig.model;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import dz.eadn.sig.util.WITHUUID;
import lombok.*;

/**
 * @author Achrouf Abdenour && AMEUR LAMOUR
 *
 */

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "sig", name = "user_notification")
public class UserNotification extends WITHUUID {

	private String message;

	// @Enumerated(EnumType.STRING)
	private String object;

	// @Enumerated(EnumType.STRING)
	private String level;

	// @Enumerated(EnumType.STRING)
	private String operation;

	private String icon;

	private String link;

	private Boolean viewed;

	@Temporal(TemporalType.TIMESTAMP)
	private Date viewedDate;

	@JoinColumn(foreignKey = @ForeignKey(name = "fk_usernotification_user_id"))
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
}
