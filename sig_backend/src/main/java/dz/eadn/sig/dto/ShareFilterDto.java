package dz.eadn.sig.dto;

import dz.eadn.sig.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LOKBANI Chouaib
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShareFilterDto {

    List<UserDto> users;

    FilterDto filter;

    String layerSlug;

    HashMap<String, Boolean> usersWithPermission;

}
