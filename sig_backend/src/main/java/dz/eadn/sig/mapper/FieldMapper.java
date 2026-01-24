package dz.eadn.sig.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.FieldDto;
import dz.eadn.sig.dto.ResourceDto;
import dz.eadn.sig.model.Field;
import dz.eadn.sig.model.Resource;
import dz.eadn.sig.service.FieldService;

import java.util.UUID;

/**
 * @author Achrouf Abdenour
 *
 */
@Component
public class FieldMapper extends CommonMapper<Field, FieldDto> {

	@Autowired
	private FieldService fieldService;

	@Autowired
	private ResourceMapper rm;

	@Override
	protected FieldDto mapEntityToDto(Field entity) {
		FieldDto fieldDto = new FieldDto();
		fieldDto.setName(entity.getName());
		fieldDto.setSlug(entity.getSlug());
		fieldDto.setRequired(entity.getRequired());
		fieldDto.setType(entity.getType());
		fieldDto.setSlug(entity.getSlug());
		fieldDto.setOrder(entity.getOrder());
		fieldDto.setVisible(entity.getVisible());
		fieldDto.setPublique(entity.getPublique());

		if (entity.getResource() != null) {
			ResourceDto resourceDto = rm.entityToDto(entity.getResource());
			fieldDto.setResourceDto(resourceDto);
		}
		return fieldDto;
	}

	@Override
	protected Field mapDtoToEntity(FieldDto dto) {
		Field field = fieldService.findById(dto.getId(), true);

		if (field == null) {
			if (dto.getId() != null) {
				return null;
			}
			field = new Field();
		} else {
			if (field.getDeleted())
				throw new RuntimeException("can't do operaion on deleted field");
		}

		if (dto.getName() != null) {
			field.setName(dto.getName());
		}

		if (dto.getOrder() != null) {
			field.setOrder(dto.getOrder());
		}

		if (dto.getType() != null) {
			field.setType(dto.getType());
		}

		if (dto.getSlug() != null) {
			field.setSlug(dto.getSlug());
		}

		if (dto.getRequired() != null) {
			field.setRequired(dto.getRequired());
		}

		if (dto.getVisible() != null) {
			field.setVisible(dto.getVisible());
		}

		if (dto.getPublique() != null) {
			field.setPublique(dto.getPublique());
		}

		if (dto.getDeleted() != null) {
			field.setDeleted(dto.getDeleted());
		}

		if(dto.getParent() != null){
			field.setParent(UUID.fromString(dto.getParent()));
		}else{
			field.setParent(null);
		}

		if (dto.getResourceDto() != null) {
			Resource resource = rm.dtoToEntity(dto.getResourceDto());
			field.setResource(resource);
		}
		return field;
	}
}
