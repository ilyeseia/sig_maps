package dz.eadn.sig.repository;

import org.springframework.context.annotation.Primary;

import dz.eadn.sig.model.Resource;
import dz.eadn.sig.repository.common.CommonRepository;

import java.util.List;
import java.util.UUID;

/**
 * @author Achrouf Abdenour
 *
 */
@Primary
public interface ResourceRepository extends CommonRepository<Resource> {

	Resource findByName(String name);

	List<Resource> findResourceByParentResource_Id(UUID resourceID);

	int countAllByParentResource_Id(UUID parent);

}
