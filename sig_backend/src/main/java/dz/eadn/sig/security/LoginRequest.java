package dz.eadn.sig.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * @author Ameur LAMOUR
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginRequest {

	@NotBlank(message = "Username is required")
	private String username;

	@NotBlank(message = "Password is required")
	private String password;

}
