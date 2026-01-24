package dz.eadn.sig.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import dz.eadn.sig.model.MapLayerId;
import dz.eadn.sig.model.MapManipulation;
import dz.eadn.sig.model.Theme;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MapLayerDto {

	private UUID mapLayerId;

	private MapLayerId id;

	private int order;

	private Boolean isVisible;

	@JsonProperty("map")
	private MapSimpleDto map;

	@JsonProperty("layer")
	private LayerSimpleDto layer;

	private LayerStyleDto layerStyle;

	private MapManipulation mapManipulation;

	@JsonIgnore
	private Theme theme;

	private ThemeDto targetTheme;

}
