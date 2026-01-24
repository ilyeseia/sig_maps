package dz.eadn.sig.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import dz.eadn.sig.util.WITHUUID;
import lombok.*;

/**
 * @author Ameur LAMOUR
 *
 */
@Getter @Setter
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
