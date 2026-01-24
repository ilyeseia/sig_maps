package dz.eadn.sig.dto;

import java.util.List;

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
public class EntityElementDto extends WITHUUID {
	private String type = "entityelements";

	private String featureJson;

	private String layerSlug;

	private String layerIdentifiant;

	@JsonProperty("tags")
	private List<TagDto> tagDtos;
}
