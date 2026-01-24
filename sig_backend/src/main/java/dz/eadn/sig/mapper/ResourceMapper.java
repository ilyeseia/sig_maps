package dz.eadn.sig.mapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.ResourceDto;
import dz.eadn.sig.model.Resource;
import dz.eadn.sig.model.ResourceValue;
import dz.eadn.sig.service.ResourceService;

/**
 * @author Achrouf Abdenour
 *
 */
@Component
public class ResourceMapper extends CommonMapper<Resource, ResourceDto> {

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private ResourceValueMapper resouceValueMapper;

	@Override
	protected ResourceDto mapEntityToDto(Resource entity) {
		ResourceDto resourceDto = new ResourceDto();
		resourceDto.setId(entity.getId());
		resourceDto.setName(entity.getName());
		resourceDto.setCode(entity.getCode());
		if(entity.getParentResource() != null){
			resourceDto.setParentResource(mapEntityToDto(entity.getParentResource()));
		}
		return resourceDto;
	}

	@Override
	protected Resource mapDtoToEntity(ResourceDto dto) {
		Resource resource = resourceService.findById(dto.getId(), true);

		if (resource == null) {
			if (dto.getId() != null) {
				return null;
			}
			resource = new Resource();
		} else {
			if (resource.getDeleted())
				throw new RuntimeException("can't do operation on deleted resource");
		}

		if (dto.getName() != null) {
			resource.setName(dto.getName());
		}

		if (dto.getCode() != null) {
			resource.setCode(dto.getCode());
		}

		if(dto.getParentResource() != null){
			resource.setParentResource(mapDtoToEntity(dto.getParentResource()));
		}

//		if (dto.getResourceValueDtos() != null) {
//
//			List<ResourceValue> newRV = resouceValueMapper.dtosToEntitys(dto.getResourceValueDtos());
//
//			if (resource.getResourceValues() != null) {
//				resource.getResourceValues().clear();
//			}
//			final Resource r = resource;
//			newRV.stream().filter(rv -> rv.getResource() == null).forEach(rv -> {
//				rv.setResource(r);
//			});
//
//			resource.getResourceValues().addAll(newRV);
//
//		}

		return resource;
	}
}
