package dz.eadn.sig.dto;

import jakarta.validation.constraints.NotBlank;

import dz.eadn.sig.util.WITHUUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Achrouf Abdenour && Ameur LAMOUR
 *
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SettingsDto extends WITHUUID {

	@NotBlank(message = "Veuillez saisir le code !")
	private String code;

	@NotBlank(message = "Veuillez selectionner le type !")
	private String type;

	@NotBlank(message = "Veuillez saisir la valeur !")
	private String value;

	private Boolean default_value;

	private Boolean enabled;
}
