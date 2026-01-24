
package dz.eadn.sig.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import dz.eadn.sig.model.Group;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.Map;
import dz.eadn.sig.repository.common.CommonRepository;

import java.util.UUID;

/**
 * @author Achrouf Abdenour && A.LAMOUR
 *
 */
@Primary
public interface GroupRepository extends CommonRepository<Group> {

	Group findByName(String name);

	Group findByNameAndIdNotLike(String name, UUID uuid);

	Page<Group> findByMaps(Map map, Pageable pageable);

	Page<Group> findByLayers(Layer layer, Pageable pageable);

}
