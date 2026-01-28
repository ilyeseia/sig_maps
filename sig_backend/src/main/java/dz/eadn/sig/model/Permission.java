package dz.eadn.sig.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import dz.eadn.sig.util.WITHUUID;
import lombok.*;

/**
 * @author Ameur LAMOUR
 *
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "sig", name = "permissions", uniqueConstraints = {
		@UniqueConstraint(columnNames = "name", name = "uk_permissions_name") })
public class Permission extends WITHUUID {
	private String label;
	private String name;
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions")
	private List<Group> groups;
}
