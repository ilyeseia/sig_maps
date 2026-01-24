/**
 * 
 */
package dz.eadn.sig.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import dz.eadn.sig.util.WITHUUID;
import lombok.*;

/**
 * @author Achrouf Abdenour && AMEUR LAMOUR
 *
 */

@Getter @Setter
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
