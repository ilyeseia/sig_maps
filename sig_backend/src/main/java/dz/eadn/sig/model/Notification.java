/**
 * 
 */
package dz.eadn.sig.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import dz.eadn.sig.util.WITHUUID;
import lombok.*;

/**
 * @author Achrouf Abdenour
 *
 */

@Entity
@Table(schema = "sig", name = "notification")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends WITHUUID {

	@JoinColumn(foreignKey = @ForeignKey(name = "fk_notification_layer_id"))
	@OneToOne(fetch = FetchType.LAZY)
	private Layer layer;

	private String template;
}
