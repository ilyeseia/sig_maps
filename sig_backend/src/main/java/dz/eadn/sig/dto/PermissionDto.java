package dz.eadn.sig.dto;

import javax.validation.constraints.NotBlank;

import dz.eadn.sig.util.WITHUUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Ameur LAMOUR
 *
 */
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PermissionDto extends WITHUUID {

	@NotBlank(message = "Veuillez saisir le code !")
	private String label;
	
	@NotBlank(message = "Veuillez saisir le nom !")
	private String name;

	private Boolean isNew;

	private Boolean toDelete;
}
