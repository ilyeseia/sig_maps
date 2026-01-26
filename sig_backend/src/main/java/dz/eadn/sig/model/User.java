package dz.eadn.sig.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.CreatedDate;

import dz.eadn.sig.util.WITHUUID;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Ameur LAMOUR
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Entity
@Table(schema = "sig", name = "user", uniqueConstraints = {
		@UniqueConstraint(columnNames = "user_name", name = "uk_user_username"),
		@UniqueConstraint(columnNames = "email", name = "uk_user_email") })

@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class User extends WITHUUID implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Schema(description = "first name", required = true)
	@Column(name = "first_name")
	private String firstName = null;

	@Schema(description = "last name", required = true)
	@Column(name = "last_name")
	private String lastName;

	@Schema(description = "home phone")
	@Column(name = "home_phone")
	private String homePhone;

	@Schema(description = "mobile")
	private String mobile;

	@Schema(description = "fax")
	private String fax;

	@Schema(description = "activation Date")
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "activation_date")
	private Date activationDate;

	@Schema(description = "desactivation Date")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "desactivation_date")
	private Date desactivationDate;

	@Schema(description = "User Name.", required = true)
	@Column(name = "user_name")
	private String username;

	private String avatar;

	@Schema(description = "password of  username.", required = true)
	@com.fasterxml.jackson.annotation.JsonIgnore
	private String password;

	@Schema(description = "email", required = true)
	private String email;

	@Schema(description = "Weither the user account is enabled.")
	private Boolean enabled = true;

	@Schema(description = "The Group which the user belong to.")
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
	private List<Group> groups;

	@Schema(description = "The list of layers related to one user.")
	@ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
	private List<Layer> layers;

	@Schema(description = "The list of maps related to one user.")
	@ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
	private List<Map> maps;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(schema = "sig", joinColumns = {
			@JoinColumn(foreignKey = @ForeignKey(name = "fk_entity_element_user_id")) }, inverseJoinColumns = {
					@JoinColumn(foreignKey = @ForeignKey(name = "fk_user_entity_element_id")) })
	private List<EntityElement> entityElements;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserNotification> notifications;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserLayerFilter> userLayerFilters = new ArrayList<>();

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof User)) {
			return false;
		}
		User u = (User) o;
		return this.getId().equals(u.getId());
	}
}
