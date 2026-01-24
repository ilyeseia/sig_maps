package dz.eadn.sig.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.PermissionDto;
import dz.eadn.sig.model.Permission;
import dz.eadn.sig.service.PermissionService;

/**
 * @author Ameur LAMOUR
 *
 */
@Component
public class PermissionMapper extends CommonMapper<Permission, PermissionDto> {

	@Autowired
	private PermissionService permissionService;

	@Override
	protected PermissionDto mapEntityToDto(Permission entity) {
		PermissionDto permissionDto = new PermissionDto();
		permissionDto.setName(entity.getName());
		permissionDto.setLabel(entity.getLabel());
		return permissionDto;
	}

	@Override
	protected Permission mapDtoToEntity(PermissionDto dto) {
		Permission permission = permissionService.findById(dto.getId(), true);

		if (permission == null) {
			if (dto.getId() != null) {
				return null;
			}
			permission = new Permission();
		} else {
			if (permission.getDeleted())
				throw new RuntimeException("can't do operation on deleted permission");
		}

		if (dto.getName() != null) {
			permission.setName(dto.getName());
		}

		if (dto.getLabel() != null) {
			permission.setLabel(dto.getLabel());
		}

		return permission;
	}
}
