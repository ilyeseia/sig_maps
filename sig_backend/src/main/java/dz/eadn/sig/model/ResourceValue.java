package dz.eadn.sig.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.*;
import org.hibernate.annotations.Type;

import dz.eadn.sig.util.WITHUUID;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Ameur LAMOUR
 *
 */
@Entity
@Table(schema = "sig", name = "resource_value")
//, uniqueConstraints = {@UniqueConstraint(columnNames = { "value", "resource_id" }, name = "uk_rv_value_resource") }
@Getter @Setter
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

	@Type(type = "pg-uuid")
	@Column(columnDefinition = "uuid", updatable = false)
	@Schema(description = "The id of resourceValue")
	private UUID parentId;

	@Type(type = "pg-uuid")
	@Column(columnDefinition = "uuid", updatable = false)
	@Schema(description = "The id of resource")
	private UUID refValue;
}
