package dz.eadn.sig.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.*;

import dz.eadn.sig.util.WITHUUID;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Ameur LAMOUR
 *
 */
@Entity
@Table(schema = "sig", name = "resource_value")
// , uniqueConstraints = {@UniqueConstraint(columnNames = { "value",
// "resource_id" }, name = "uk_rv_value_resource") }
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResourceValue extends WITHUUID {

	@Schema(description = "The value of resource", example = "[Adrar,Chlef,Laghouat]", required = true)
	private String value;

	@Schema(description = "The id of resource", required = true)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_resource_id"), name = "resource_id")
	private Resource resource;

	@Column(columnDefinition = "uuid", updatable = false)
	@Schema(description = "The id of resourceValue")
	private UUID parentId;

	@Column(columnDefinition = "uuid", updatable = false)
	@Schema(description = "The id of resource")
	private UUID refValue;
}
