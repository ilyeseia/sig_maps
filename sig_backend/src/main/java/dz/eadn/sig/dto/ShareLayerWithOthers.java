package dz.eadn.sig.dto;

import java.util.ArrayList;


import java.util.List;

import dz.eadn.sig.model.Group;
import dz.eadn.sig.model.User;
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
public class ShareLayerWithOthers {
	private List<UserDto> users = new ArrayList<>();
	private List<GroupDto> groups = new ArrayList<>();;
}
