package dz.eadn.sig.model;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import dz.eadn.sig.util.WITHUUID;
import lombok.*;

/**
 * @author Ameur LAMOUR
 *
 */
@Entity
@Table(schema = "sig", name = "settings", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "code" }, name = "uk_settings_code"), })
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Settings extends WITHUUID {
	private String code;

	@ManyToOne
	@JoinColumn(name = "type_id", foreignKey = @ForeignKey(name = "fk_settings_type_id"))
	private SettingsType type;

	private String value;
	private Boolean default_value = false;
	private Boolean enabled = true;
}
