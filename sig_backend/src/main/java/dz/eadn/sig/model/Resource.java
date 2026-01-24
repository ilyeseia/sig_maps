package dz.eadn.sig.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import dz.eadn.sig.util.WITHUUID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * @author Ameur LAMOUR
 *
 */
@Entity
@Table(schema = "sig", name = "resource", uniqueConstraints = {
		@UniqueConstraint(columnNames = "name", name = "uk_resource_name") })
@Getter @Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Resource extends WITHUUID {

	@Schema(description = "The name of the resource.", example = "Liste Wilaya", required = true)
	private String name;

	@Schema(description = "The code of the resource.", example = "RES_001", required = true)
	private String code;

	@Schema(description = "The list fields of the resource.")
	@OneToMany(mappedBy = "resource", fetch = FetchType.LAZY)
	private List<Field> fields;

	@JoinColumn(foreignKey = @ForeignKey(name = "fk_resource_resource_id"))
	@OneToOne(fetch = FetchType.EAGER)
	private Resource parentResource;

//	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<Resource> resourceList;

	@Schema(description = "The list values of the resource.")
	@Column(name = "resource_values")
	@OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<ResourceValue> resourceValues = new ArrayList<>();
}
