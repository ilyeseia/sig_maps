package dz.eadn.sig.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Primary;

import dz.eadn.sig.model.ResourceValue;
import dz.eadn.sig.repository.common.CommonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Achrouf Abdenour, LOKBANI Chouaib
 *
 */
@Primary
public interface ResourceValueRepository extends CommonRepository<ResourceValue> {

	Page<ResourceValue> findAllByResource_IdAndValueContainsIgnoreCase(UUID uuid, String value, Pageable pageable);

	List<ResourceValue> findAllByResource_Id(UUID uuid);

	Page<ResourceValue> findAllByResource_IdAndParentIdAndValueContainsIgnoreCase(UUID uuid,UUID rvParentId, String value, Pageable pageable);

	int countByParentIdAndValueIgnoreCaseAndIdNot(UUID resourceId, String value, UUID id);

	int countByResource_IdAndValueIgnoreCaseAndIdNot(UUID resourceId, String value, UUID id);

	int countByResource_IdAndValueIgnoreCase(UUID resourceId, String value);

	int countByResource_IdAndParentIdAndValueIgnoreCase(UUID resourceId, UUID rvParentId, String value);

	void deleteAllByResource_Id(UUID uuid);

	void deleteAllByParentId(UUID uuid);

	List<ResourceValue> findAllByParentId(UUID uuid);

	int countAllByParentId(UUID uuid);

}
