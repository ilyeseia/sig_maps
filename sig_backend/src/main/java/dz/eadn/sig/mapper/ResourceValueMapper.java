package dz.eadn.sig.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.ResourceValueDto;
import dz.eadn.sig.model.Resource;
import dz.eadn.sig.model.ResourceValue;
import dz.eadn.sig.service.ResourceService;
import dz.eadn.sig.service.ResourceValueService;

/**
 * @author Achrouf Abdenour
 *
 */
@Component
public class ResourceValueMapper extends CommonMapper<ResourceValue, ResourceValueDto> {

	@Autowired
	private ResourceValueService rvService;

	@Autowired
	private ResourceService resourceService;

	@Override
	protected ResourceValueDto mapEntityToDto(ResourceValue entity) {
		ResourceValueDto rvDto = new ResourceValueDto();
		rvDto.setValue(entity.getValue());
		if (entity.getResource() != null)
			rvDto.setResourceId(entity.getResource().getId());
		rvDto.setParentId(entity.getParentId());
		rvDto.setRefValue(entity.getRefValue());
		return rvDto;
	}

	@Override
	protected ResourceValue mapDtoToEntity(ResourceValueDto dto) {
		ResourceValue rv = rvService.findById(dto.getId(), true);

		if (rv == null) {
			if (dto.getId() != null) {
				return null;
			}
			rv = new ResourceValue();
		} else {
			if (rv.getDeleted())
				throw new RuntimeException("can't do operation on deleted resource value");
		}

		if (dto.getValue() != null) {
			rv.setValue(dto.getValue());
		}
		if (dto.getResourceId() != null) {
			Resource resource = resourceService.findById(dto.getResourceId());
			rv.setResource(resource);
		}
		if (dto.getParentId() != null) {
			rv.setParentId(dto.getParentId());
		}
		if (dto.getRefValue() != null) {
			rv.setRefValue(dto.getRefValue());
		}

		/*
		 * if (dto.getResourceDto() != null) { Resource resource =
		 * resourceMapper.dtoToEntity(dto.getResourceDto()); rv.setResource(resource); }
		 */
		return rv;
	}
}
