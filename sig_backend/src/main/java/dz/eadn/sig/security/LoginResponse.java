package dz.eadn.sig.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ameur LAMOUR
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginResponse {
	private String accessToken;
	private String refreshToken;
}
