package dz.eadn.sig.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dz.eadn.sig.model.Privacy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareMapWithOthers {
	private Privacy privacy;
	private List<UserDto> users = new ArrayList<>();
	private List<GroupDto> groups = new ArrayList<>();
}
