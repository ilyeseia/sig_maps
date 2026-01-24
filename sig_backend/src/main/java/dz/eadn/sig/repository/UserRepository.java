package dz.eadn.sig.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dz.eadn.sig.dto.UserProjection;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import dz.eadn.sig.model.Group;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.Map;
import dz.eadn.sig.model.User;
import dz.eadn.sig.repository.common.CommonRepository;
import org.springframework.data.jpa.repository.Query;


/**
 * @author A.LAMOUR
 *
 */
@Primary
public interface UserRepository extends CommonRepository<User> {

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String mail);

	Page<User> findByMaps(Map map, Pageable pageable);
	
	Page<User> findByLayers(Layer layer, Pageable pageable);

	Page<User> findByFirstNameContainingAndLayersOrLastNameContainingAndLayers(String lastname, Layer layer1, String firstname, Layer layer2, Pageable pageable);

	Page<User> findByGroups(Group group, Pageable pageable);

	List<UserProjection> findBy();

}
