package dz.eadn.sig.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProfileDto extends UserSimpleDto {

	private List<String> claims = new ArrayList<>();

	private List<LayerSimpleDto> layers = new ArrayList<>();

	private List<MapSimpleDto> maps = new ArrayList<>();

	private List<GroupSimpleDto> groups = new ArrayList<>();

	@JsonProperty("notifications")
	private PageDto<UserNotificationDto> notificationDtos = new PageDto<>();
}
