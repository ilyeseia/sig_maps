package dz.eadn.sig.service;

import dz.eadn.sig.dto.ResourceDto;
import dz.eadn.sig.model.Resource;
import dz.eadn.sig.service.common.CommonService;

import java.util.List;
import java.util.UUID;

/**
 * @author Achrouf Abdenour, LOKBANI Chouaib
 *
 */
public interface ResourceService extends CommonService<Resource, ResourceDto> {

    List<ResourceDto> getAllResourceChildren(UUID resourceID);

}
