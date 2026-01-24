package dz.eadn.sig.mapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.GroupDto;
import dz.eadn.sig.model.Group;
import dz.eadn.sig.model.Permission;
import dz.eadn.sig.model.User;
import dz.eadn.sig.service.GroupService;

/**
 * @author Achrouf Abdenour
 *
 */
@Component
public class GroupMapper extends CommonMapper<Group, GroupDto> {

	@Autowired
	private GroupService groupService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private LayerMapper layerMapper;

	@Autowired
	private PermissionMapper permissionMapper;

	@Override
	protected GroupDto mapEntityToDto(Group entity) {
		GroupDto groupDto = new GroupDto();
		groupDto.setName(entity.getName());
		groupDto.setLabel(entity.getLabel());
		groupDto.setDescription(entity.getDescription());

		if (entity.getLayers() != null)
			groupDto.setLayerDtos(layerMapper.entitysToDtos(entity.getLayers()));

		if (entity.getUsers() != null)
			groupDto.setUserDtos(userMapper.entitysToDtos(entity.getUsers()));

		if (entity.getPermissions() != null)
			groupDto.setPermissions(permissionMapper.entitysToDtos(entity.getPermissions()));

		return groupDto;
	}

	@Override
	protected Group mapDtoToEntity(GroupDto dto) {
		Group group = groupService.findById(dto.getId(), true);

		if (group == null) {
			if (dto.getId() != null) {
				return null;
			}

			group = new Group();
		}

		if (dto.getName() != null)
			group.setName(dto.getName());

		if (dto.getLabel() != null)
			group.setLabel(dto.getLabel());

		if (dto.getDescription() != null)
			group.setDescription(dto.getDescription());

		if (dto.getUserDtos() != null) {
			List<User> oldUsers = group.getUsers();
			List<User> users = userMapper.dtosToEntitys(dto.getUserDtos());

			if (oldUsers != null && users != null) {
				oldUsers.removeAll(users);
			}

			if (oldUsers != null) {
				oldUsers.stream().forEach(user -> user.setGroups(null));
			}

			// Group p = group;
			// users.stream().filter(user -> user.getGroups() == null).forEach(user ->
			// user.setGroups(p));
			group.setUsers(users);

		}

		if (dto.getPermissions() != null) {

			List<Permission> oldPermissions = group.getPermissions();
			List<Permission> permissions = permissionMapper.dtosToEntitys(dto.getPermissions());

			if (oldPermissions != null && permissions != null) {
				oldPermissions.removeAll(permissions);
			}

			group.setPermissions(permissions);
		}

		return group;
	}
}
