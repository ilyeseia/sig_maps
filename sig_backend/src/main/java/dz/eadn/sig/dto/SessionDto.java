package dz.eadn.sig.dto;

import dz.eadn.sig.util.WITHUUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Achrouf Abdenour
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SessionDto extends WITHUUID {
	public SessionDto(String userName, String token) {
		this.userName = userName;
		this.token = token;
	}

	private String userName;
	private boolean enabled;
	private String email;
	private String avatar;
	private String token;
	private boolean tokenExpired;
}
