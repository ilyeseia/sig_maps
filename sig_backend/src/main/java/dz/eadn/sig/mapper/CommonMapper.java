package dz.eadn.sig.mapper;

import java.util.List;
import java.util.stream.Collectors;

import dz.eadn.sig.util.WITHUUID;

/**
 * @author Achrouf Abdenour
 *
 * @param <CommonObject>
 * @param <CommonDto>
 */
public abstract class CommonMapper<CommonObject extends WITHUUID, CommonDto extends WITHUUID> {

	protected abstract CommonDto mapEntityToDto(CommonObject entity);

	protected abstract CommonObject mapDtoToEntity(CommonDto dto);

	public CommonDto entityToDto(CommonObject entity) {
		if (entity == null) {
			return null;
		}

		if (entity.getDeleted())
			return null;

		CommonDto dto = mapEntityToDto(entity);

		// this will copy common entity
		// propreties[id,deleted,createdBy,createdDate,modifiedBy,lastModifiedBy] to
		// dto
		copyToDto(entity, dto);
		return dto;
	}

	public CommonObject dtoToEntity(CommonDto dto) {
		CommonObject entity = mapDtoToEntity(dto);
		if (entity == null) {
			return null;
		}

		// this will copy common dto
		// propreties[id,deleted,createdBy,createdDate,modifiedBy,lastModifiedBy] to
		// entity
		copyToEntity(dto, entity);
		return entity;
	}

	public List<CommonObject> dtosToEntitys(List<CommonDto> dtos) {
		return dtos.stream().map(dto -> dtoToEntity(dto)).filter(e -> e != null).collect(Collectors.toList());
	}

	public List<CommonDto> entitysToDtos(List<CommonObject> entitys) {
		return entitys.stream().map(dto -> entityToDto(dto)).filter(dto -> dto != null).collect(Collectors.toList());
	}

	public void copyToDto(CommonObject entity, WITHUUID dto) {
		dto.setId(entity.getId());
		dto.setCreatedBy(entity.getCreatedBy());
		dto.setCreateDate(entity.getCreateDate());
		dto.setDeleted(entity.getDeleted());
		dto.setModifiedBy(entity.getModifiedBy());
		dto.setLastModifiedDate(entity.getLastModifiedDate());
	}

	public void copyToEntity(WITHUUID dto, CommonObject entity) {
		if (dto.getId() != null)
			entity.setId(entity.getId());
		if (dto.getDeleted() != null)
			entity.setDeleted(entity.getDeleted());

		if (dto.getCreatedBy() != null)
			entity.setCreatedBy(entity.getCreatedBy());
		if (dto.getCreateDate() != null)
			entity.setCreateDate(entity.getCreateDate());
		if (dto.getLastModifiedDate() != null)
			entity.setLastModifiedDate(entity.getLastModifiedDate());
		if (dto.getModifiedBy() != null)
			entity.setModifiedBy(dto.getModifiedBy());
	}
}
