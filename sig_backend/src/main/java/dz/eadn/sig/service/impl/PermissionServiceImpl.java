package dz.eadn.sig.service.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dz.eadn.sig.dto.PermissionDto;
import dz.eadn.sig.exceptions.EntityAlreadyExistsException;
import dz.eadn.sig.mapper.PermissionMapper;
import dz.eadn.sig.model.Permission;
import dz.eadn.sig.repository.PermissionRepository;
import dz.eadn.sig.service.PermissionService;
import dz.eadn.sig.service.common.impl.CommonServiceImpl;

/**
 * @author Ameur.LAMOUR
 *
 */
@Service
public class PermissionServiceImpl extends CommonServiceImpl<Permission, PermissionDto> implements PermissionService {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private PermissionMapper permissionMapper;

	public PermissionServiceImpl() {
		super(Permission.class);
	}

	@Override
	public PermissionDto save(PermissionDto permissionDto) {
		if (permissionDto != null) {

			Permission existpermission = permissionRepository.findByName(permissionDto.getName());

			if (existpermission != null && !permissionDto.getId().equals(existpermission.getId())) {
				throw new EntityAlreadyExistsException(
						String.format("permission avec le nom <%s> est toujours exsite ", permissionDto.getName()));
			}
		}

		Permission permission = permissionRepository.save(permissionMapper.dtoToEntity(permissionDto));

		return permissionMapper.entityToDto(permission);

	}

}
