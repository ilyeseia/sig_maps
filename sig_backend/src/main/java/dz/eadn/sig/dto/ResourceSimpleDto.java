package dz.eadn.sig.dto;

import java.util.ArrayList;
import java.util.List;
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
public class ResourceSimpleDto {
	
	private UUID id;
	private String name;
	private String code;
	private List<ResourceValueSimpleDto> resourceValues = new ArrayList<>();

}
