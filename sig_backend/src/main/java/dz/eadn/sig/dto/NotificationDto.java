/**
 * 
 */
package dz.eadn.sig.dto;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class NotificationDto extends WITHUUID {
	@JsonProperty("layer")
	private LayerDto layerDto;

	@NotBlank(message = "Veuillez saisir le paramètre !")
	private String template;
}
