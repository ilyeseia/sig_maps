package dz.eadn.sig.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

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
@Getter
@Setter
public class Settings extends WITHUUID {
	private String code;

	@ManyToOne
	@JoinColumn(name = "type_id", foreignKey = @ForeignKey(name = "fk_settings_type_id"))
	private SettingsType type;

	private String value;
	private Boolean default_value = false;
	private Boolean enabled = true;
}
