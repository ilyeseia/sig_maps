package dz.eadn.sig.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;

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
public class ResourceDto extends WITHUUID {
	
	@NotBlank(message = "Veuillez saisir le nom !")
	private String name;

	@NotBlank(message = "Veuillez saisir le code !")
	private String code;

	private String propertyExtractor;

	private ResourceDto parentResource;

	@JsonProperty("resourceValues")
	private List<ResourceValueDto> resourceValueDtos;
}
