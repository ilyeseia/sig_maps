package dz.eadn.sig.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import dz.eadn.sig.model.FieldType;
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
public class FieldDto extends WITHUUID {

	@NotBlank(message = "Veuillez saisir le nom !")
	private String name;

	@NotBlank(message = "Veuillez saisir le type !")
	private FieldType type;

	private String slug;
	private Boolean required;
	private Integer order;

	private Boolean visible;
	private Boolean publique = true;
	private String parent;
	private String layer;

	@JsonProperty("resource")
	private ResourceDto resourceDto;
}
