package dz.eadn.sig.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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
public class UserCompleteDto extends UserSimpleDto {

	private Boolean enabled = true;
	private String homePhone;
	private String mobile;
	private String fax;
	private Date activationDate;
	private Date desactivationDate;
	private String createdBy;
	private Date createDate;
	@JsonProperty("divisions")
	private List<DivisionDto> divisionDtos;
	@JsonProperty("groups")
	private List<GroupSimpleDto> groupDtos;

}
