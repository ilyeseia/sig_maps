package dz.eadn.sig.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import dz.eadn.sig.util.WITHUUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Achrouf Abdenour
 *
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto extends WITHUUID {

	@NotBlank(message = "Veuillez saisir le nom !")
	private String firstName;

	@NotBlank(message = "Veuillez saisir le; pr�nom !")
	private String lastName;

	private String homePhone;
	private String mobile;
	private String fax;

	@Temporal(TemporalType.TIMESTAMP)
	private Date activationDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date desactivationDate;

	@NotBlank(message = "Veuillez saisir le nom d'utilisateur !")
	private String username;

	@NotBlank(message = "Veuillez saisir le mot de passe !")
	private String password;

	@NotBlank(message = "Veuillez saisir l'email !")
	@Email(message = "Veuillez saisir un email valide!")
	private String email;

	private String avatar;

	private Map<String, String> message;

	private Boolean enabled = true;

	@JsonProperty("groups")
	private List<GroupSimpleDto> groupDtos;

	@JsonProperty("layers")
	private List<LayerDto> layersDtos;

	@JsonProperty("divisions")
	private List<DivisionDto> DivisionDtos;

	@JsonProperty("notifications")
	@JsonIgnore
	private List<UserNotificationDto> notificationDtos;

	private List<String> claims;

	private boolean tokenExpired;

	private Boolean isNew;

	private Boolean toDelete;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof UserDto)) {
			return false;
		}
		UserDto u = (UserDto) o;
		return this.getId().equals(u.getId());
	}
}