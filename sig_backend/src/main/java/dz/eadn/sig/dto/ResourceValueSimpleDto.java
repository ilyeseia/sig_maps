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

public class ResourceValueSimpleDto {
	private UUID id;
	private String value;
	private UUID resourceId;
	private UUID parentId;
	private UUID refValue;
}
