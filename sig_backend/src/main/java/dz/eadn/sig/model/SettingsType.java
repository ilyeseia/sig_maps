package dz.eadn.sig.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import dz.eadn.sig.util.WITHUUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Ameur LAMOUR
 *
 */
@Entity
@Table(schema = "sig", name = "settings_type", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "code" }, name = "uk_settings_type_code"), })
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SettingsType extends WITHUUID {
	private String code;
	private String description;
	private Boolean default_value;
	private Boolean enabled = true;
}
