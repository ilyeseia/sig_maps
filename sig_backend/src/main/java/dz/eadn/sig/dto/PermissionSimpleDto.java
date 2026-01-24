package dz.eadn.sig.dto;

import java.util.UUID;

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
public class PermissionSimpleDto {

	private UUID id;
	private String label;
	private String name;
}
