package dz.eadn.sig.model;

import java.util.List;
import java.util.Map;

import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.locationtech.jts.geom.Geometry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import dz.eadn.sig.util.WITHUUID;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Ameur LAMOUR
 *
 */
@Entity
@Table(schema = "sig", name = "entity_element")
@Getter
@Setter
@EqualsAndHashCode(exclude = "layer")
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class EntityElement extends WITHUUID {

	@Schema(description = "The geometry of the elementEntity.", example = "Point", required = true)
	@JsonIgnore
	private Geometry geom;

	@Schema(description = "The properties of the elementEntity.", required = true)
	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	@Basic(fetch = FetchType.LAZY)
	private Map<String, String> properties;

	@Schema(description = "The layer id of the elementEntity.", required = true)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "layer_entity_element", foreignKey = @ForeignKey(name = "fk_layer_entity_element_id"))
	private Layer layer;

	@Schema(description = "The tags of the elementEntity.", required = true)
	@ManyToMany(mappedBy = "entityElements", fetch = FetchType.LAZY)
	private List<Tag> tags;

	@ManyToMany(mappedBy = "entityElements", fetch = FetchType.LAZY)
	private List<User> users;
}