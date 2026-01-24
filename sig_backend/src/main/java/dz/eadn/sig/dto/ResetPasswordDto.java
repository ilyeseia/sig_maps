/**
 * 
 */
package dz.eadn.sig.dto;

import java.util.UUID;

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
public class ResetPasswordDto {
	private UUID userId;
	private String oldPassword;
	private String newPassword;
}
