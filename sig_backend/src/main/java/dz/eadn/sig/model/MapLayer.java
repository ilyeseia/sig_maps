package dz.eadn.sig.model;

import jakarta.persistence.*;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(schema = "sig", name = "map_layers", uniqueConstraints = {
		@UniqueConstraint(columnNames = "mapLayerId", name = "uk_map_layer") })
public class MapLayer implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(columnDefinition = "uuid", updatable = false)
	private UUID mapLayerId;

	@EmbeddedId
	private MapLayerId id;

	@ManyToOne
	@MapsId("mapId")
	@JoinColumn(name = "maps_id", foreignKey = @ForeignKey(name = "fk_map_layer_id"))
	private Map map;

	@ManyToOne
	@MapsId("layerId")
	@JoinColumn(name = "layers_id", foreignKey = @ForeignKey(name = "fk_layer_map_id"))
	private Layer layer;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mapLayer", cascade = CascadeType.ALL)
	private List<Style> styles = new ArrayList<>();

	@Column(name = "layer_order")
	private int order = 1;

	private Boolean isVisible = true;

	public MapLayer(Map map, Layer layer, int order, Boolean isVisible) {
		this.mapLayerId = UUID.randomUUID();
		this.id = new MapLayerId(UUID.randomUUID(), map.getId(), layer.getId());
		this.map = map;
		this.layer = layer;
		this.order = order;
		if (isVisible != null)
			this.isVisible = isVisible;
	}

}
