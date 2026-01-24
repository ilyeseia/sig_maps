package dz.eadn.sig.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.dto.ResourceValueDto;
import dz.eadn.sig.model.Resource;
import dz.eadn.sig.model.ResourceValue;
import dz.eadn.sig.service.common.CommonService;
import org.springframework.data.domain.Pageable;

/**
 * @author Achrouf Abdenour, LOKBANI Chouaib
 *
 */
public interface ResourceValueService extends CommonService<ResourceValue, ResourceValueDto> {
	public List<ResourceValue> findAllByResouceIdAndParentId(UUID resourceId, UUID parentId);

	public List<List<String>> readResourceValues(InputStream is) throws FileNotFoundException, IOException;

	PageDto<ResourceValueDto> findByValueAndResource( String resourceId,String searchedValue, Integer page, Integer limit, String sort, String dir);

	PageDto<ResourceValueDto> findByValueAndResourceAndParent( String resourceId,String rvParentId,  String searchedValue, Integer page, Integer limit, String sort, String dir);

	boolean checkExistenceByParent(UUID parentId, String value, UUID rvId);

	boolean checkExistenceByResource(UUID resourceId, String value, UUID rvId);

	boolean checkExistenceByResource(UUID resourceId,  UUID parentId, String value);

	HashMap<String, List<ResourceValueDto>> importResourceValues(List<ResourceValueDto> resourceValueDtos, boolean withMissingValue);

	List<UUID> deleteAllResourceValuesByResource(UUID uuid);

	List<UUID> deleteAllByParentId(UUID uuid);

}
