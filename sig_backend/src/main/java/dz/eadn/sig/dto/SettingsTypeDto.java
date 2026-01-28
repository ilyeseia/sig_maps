package dz.eadn.sig.dto;

import jakarta.validation.constraints.NotBlank;

import dz.eadn.sig.util.WITHUUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Ameur LAMOUR
 *
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SettingsTypeDto extends WITHUUID {

	@NotBlank(message = "Veuillez saisir le code !")
	private String code;

	private String description;

	private Boolean default_value;

	private Boolean enabled;
}
