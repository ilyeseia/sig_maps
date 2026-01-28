package dz.eadn.sig.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dz.eadn.sig.util.WITHUUID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * @author Ameur LAMOUR
 *
 */
@Entity
@Table(schema = "sig", name = "field", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "slug", "layer_id" }, name = "uk_field_slug_layer") })
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Field extends WITHUUID {

	@Schema(description = "Name of the field.", example = "MSAN", required = true)
	private String name;

	@Enumerated(EnumType.STRING)
	@Schema(description = "Type of the field.Can be one of Number,Text,Image,Date,Select,MultiSelect", example = "NUMBER", required = true)
	private FieldType type;
	private String slug;
	private Boolean required;

	private Boolean visible;
	private Boolean publique = true;

	private UUID parent;

	@Column(name = "field_order")
	private int order;

	@Schema(description = "Attached resource if any, a resource can be a select one or a multi select ,an item is stored as a resource value", example = "Liste des wilaya", required = false)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_field_resource_id"))
	private Resource resource;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "layer_id", foreignKey = @ForeignKey(name = "fk_field_layer_id"))
	private Layer layer;
}
