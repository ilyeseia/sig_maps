package dz.eadn.sig.dto;

import java.util.UUID;

import javax.validation.constraints.NotBlank;

import dz.eadn.sig.util.WITHUUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Achrouf Abdenour
 *
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResourceValueDto extends WITHUUID {

	@NotBlank(message = "Veuillez saisir la valeur !")
	private String value;
	private UUID resourceId;
	private UUID parentId;
	private UUID refValue;
}
