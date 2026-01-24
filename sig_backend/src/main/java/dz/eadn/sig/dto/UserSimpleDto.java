package dz.eadn.sig.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author A.LAMOUR
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserSimpleDto {

	private UUID id;
	private String firstName;
	private String lastName;
	private String username;
	private String avatar;
	private String email;

}
