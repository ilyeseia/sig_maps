package dz.eadn.sig.dto;

import java.util.List;

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
public class TagDto extends WITHUUID {
	
	@NotBlank(message = "Veuillez saisir le nom !")
	private String name;
	
	private String message;

	@JsonProperty("layers")
	private List<LayerDto> layerDtos;

	@JsonProperty("entityElements")
	private List<EntityElementDto> entityElementDtos;

	@JsonProperty("maps")
	private List<MapDto> mapDtos;
}
