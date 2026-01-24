package dz.eadn.sig.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import dz.eadn.sig.model.Group;
import dz.eadn.sig.model.Permission;
import dz.eadn.sig.repository.common.CommonRepository;

/**
 * @author Ameur LAMOUR
 *
 */
@Primary
public interface PermissionRepository extends CommonRepository<Permission> {
	Permission findByName(String name);

	Page<Permission> findByGroups(Group group, Pageable pageable);

}
