package dz.eadn.sig.dto;

import java.util.List;
import java.util.UUID;

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
public class GroupDto extends WITHUUID {

	private UUID uuid;

	@NotBlank(message = "Veuillez saisir le nom !")
	private String name;

	@NotBlank(message = "Veuillez saisir le code !")
	private String label;

	private String description;

	@JsonProperty("users")
	private List<UserDto> userDtos;

	@JsonProperty("layers")
	private List<LayerDto> layerDtos;

	private List<PermissionDto> permissions;

	private Boolean isNew;

	private Boolean toDelete;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof GroupDto)) {
			return false;
		}
		GroupDto g = (GroupDto) o;
		return this.getId().equals(g.getId());
	}
}
