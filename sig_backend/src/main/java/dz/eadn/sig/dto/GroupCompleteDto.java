package dz.eadn.sig.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class GroupCompleteDto extends GroupSimpleDto {
	private String label;
	private String createdBy;
	private Date createDate;
	@JsonProperty("users")
	List<UserSimpleDto> userDtos = new ArrayList<>();
	private List<PermissionDto> permissions = new ArrayList<>();

}
