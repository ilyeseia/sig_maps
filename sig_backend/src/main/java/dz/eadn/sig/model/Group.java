package dz.eadn.sig.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import dz.eadn.sig.util.WITHUUID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * @author Ameur LAMOUR
 *
 */
@Entity
@Table(schema = "sig", name = "group", uniqueConstraints = {
		@UniqueConstraint(columnNames = "name", name = "uk_group_name") })
@Getter @Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Group extends WITHUUID {

	@Schema(description = "User Group name", required = true)
	private String name;

	@Schema(description = "User Group label", required = true)
	private String label;

	@Schema(description = "a short description of the user group", required = true)
	private String description;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(schema = "sig", joinColumns = {
			@JoinColumn(foreignKey = @ForeignKey(name = "fk_user_groups_id")) }, inverseJoinColumns = {
					@JoinColumn(foreignKey = @ForeignKey(name = "fk_group_users_id")) })
	private List<User> users;

	@ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
	private List<Layer> layers;

	@ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
	private List<Map> maps;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(schema = "sig", joinColumns = {
			@JoinColumn(name = "group_id", foreignKey = @ForeignKey(name = "fk_group_permission_id")) }, inverseJoinColumns = {
					@JoinColumn(name="permissions_id", foreignKey = @ForeignKey(name = "fk_permission_group_id")) })
	private List<Permission> permissions;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Group)) {
			return false;
		}
		Group g = (Group) o;
		return this.getId().equals(g.getId());
	}

}
