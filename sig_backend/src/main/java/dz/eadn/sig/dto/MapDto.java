package dz.eadn.sig.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import dz.eadn.sig.model.Privacy;
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
public class MapDto extends WITHUUID {
	private String type = "maps";
	private String image;

	private Privacy privacy;

	@JsonProperty("tags")
	private List<TagDto> tagDtos;

	@NotBlank(message = "Veuillez saisir le nom !")
	private String name;

	private String slug;

	@JsonProperty("layers")
	private List<LayerDto> layerDtos;

	/*
	 * @JsonProperty("layers") private List<MapLayerDto> layerDtos = new
	 * ArrayList<>();
	 */

	@JsonProperty("groups")
	private List<GroupDto> groupDtos;

	@JsonProperty("users")
	private List<UserDto> userDtos;

	/*
	 * @JsonProperty("tags") private List<TagDto> tagDtos;
	 */
}
