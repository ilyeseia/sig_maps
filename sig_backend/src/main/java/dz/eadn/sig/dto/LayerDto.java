package dz.eadn.sig.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import dz.eadn.sig.model.LayerType;
import dz.eadn.sig.model.SymbologyType;
import dz.eadn.sig.model.TypeLimit;
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
public class LayerDto extends WITHUUID {
	// used to create geoserver layer or featuretype
	private String featureType;

	private LayerType type;

	@NotBlank(message = "Veuillez saisir le nom !")
	private String name;

	private String slug;

	private String topo;

	private String sldBody;

	private TypeLimit typeLimit;

	private Integer order;

	private String identifiant;

	private Boolean visible;

	@JsonProperty("fields")
	private List<FieldDto> fieldDtos;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private List<GroupDto> groupDtos = new ArrayList<>();;

	@JsonProperty( access = JsonProperty.Access.WRITE_ONLY)
	private List<UserDto> userDtos = new ArrayList<>();;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private List<TagDto> tagDtos;
}
