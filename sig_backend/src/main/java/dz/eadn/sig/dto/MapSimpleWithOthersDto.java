package dz.eadn.sig.dto;

import java.util.UUID;

import dz.eadn.sig.model.Privacy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author A.LAMOUR, C.LOKBANI
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapSimpleWithOthersDto {
	private UUID id;
	private String slug;
	private String createdBy;
	private Privacy privacy;
	private PageDto<UserSimpleDto> users = new PageDto<>();
	private PageDto<GroupSimpleDto> groups = new PageDto<>();
}
