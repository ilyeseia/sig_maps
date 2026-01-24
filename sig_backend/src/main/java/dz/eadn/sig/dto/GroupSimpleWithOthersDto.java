package dz.eadn.sig.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author A.LAMOUR
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupSimpleWithOthersDto {

	private UUID id;
	private String name;
	private String label;
	private String description;
	private String createdBy;
	private PageDto<UserSimpleDto> users = new PageDto<>();
	private PageDto<PermissionSimpleDto> permissions = new PageDto<>();
}
