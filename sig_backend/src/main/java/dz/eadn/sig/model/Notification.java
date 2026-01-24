/**
 * 
 */
package dz.eadn.sig.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import dz.eadn.sig.util.WITHUUID;
import lombok.*;

/**
 * @author Achrouf Abdenour
 *
 */

@Entity
@Table(schema = "sig", name = "notification")
@Getter @Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends WITHUUID {

	@JoinColumn(foreignKey = @ForeignKey(name = "fk_notification_layer_id"))
	@OneToOne(fetch = FetchType.LAZY)
	private Layer layer;

	private String template;
}
