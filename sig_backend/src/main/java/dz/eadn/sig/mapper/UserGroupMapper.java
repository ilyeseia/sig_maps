package dz.eadn.sig.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dz.eadn.sig.dto.GroupSimpleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.UserDto;
import dz.eadn.sig.model.Group;
import dz.eadn.sig.model.User;

/**
 * @author Achrouf Abdenour
 *
 */
@Component
public class UserGroupMapper extends CommonMapper<User, UserDto> {


	@Autowired
	private LayerMapper layerMapper;

	@Autowired
	private CommonModelMapper<?, ?> cModelMapper;

	/*
	 * @Autowired private UserNotificationMapper userNotificationMapper;
	 */

	@Override
	protected UserDto mapEntityToDto(User entity) {
		UserDto userDto = new UserDto();
		userDto.setFirstName(entity.getFirstName());
		userDto.setLastName(entity.getLastName());
		userDto.setHomePhone(entity.getHomePhone());
		userDto.setMobile(entity.getMobile());
		userDto.setFax(entity.getFax());
		userDto.setActivationDate(entity.getActivationDate());
		userDto.setDesactivationDate(entity.getDesactivationDate());
		userDto.setUsername(entity.getUsername());
		userDto.setEmail(entity.getEmail());
		userDto.setEnabled(entity.getEnabled());
		userDto.setAvatar(entity.getAvatar());

		if (entity.getGroups() != null)
			userDto.setGroupDtos(cModelMapper.mapList(userDto.getGroupDtos(), GroupSimpleDto.class));

		if (entity.getLayers() != null) {
			userDto.setLayersDtos(layerMapper.entitysToDtos(entity.getLayers()));
		}

		if (entity.getNotifications() != null) {
			// userDto.setNotificationDtos(userNotificationMapper.entitysToDtos(entity.getNotifications()));
		}

		List<String> claims = new ArrayList<String>();

		if (entity.getGroups() != null) {
			for (Group group : entity.getGroups()) {
				claims.addAll(group.getPermissions().stream().map(p -> p.getName()).collect(Collectors.toList()));
				if (group.getName().equals("ROLE_ADMIN"))
					claims.add(group.getName());
			}
		}

		userDto.setClaims(claims);
		return userDto;
	}

	@Override
	protected User mapDtoToEntity(UserDto dto) {
		return null;
	}
}
