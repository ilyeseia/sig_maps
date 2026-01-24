package dz.eadn.sig.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.TagDto;
import dz.eadn.sig.model.Tag;
import dz.eadn.sig.service.TagService;

/**
 * @author Achrouf Abdenour
 *
 */
@Component
public class TagMapper extends CommonMapper<Tag, TagDto> {

	@Autowired
	private TagService tagService;

	@Autowired
	private LayerMapper layerMapper;

	@Autowired
	private MapMapper mapMapper;

	@Autowired
	private EntityElementMapper entityElementMapper;

	@Override
	protected TagDto mapEntityToDto(Tag entity) {
		TagDto tagDto = new TagDto();
		tagDto.setName(entity.getName());
		tagDto.setMessage(entity.getMessage());

		if (entity.getMaps() != null)
			tagDto.setMapDtos(mapMapper.entitysToDtos(entity.getMaps()));

		if (entity.getLayers() != null)
			tagDto.setLayerDtos(layerMapper.entitysToDtos(entity.getLayers()));

		if (entity.getEntityElements() != null)
			tagDto.setEntityElementDtos(entityElementMapper.entitysToDtos(entity.getEntityElements()));

		return tagDto;
	}

	@Override
	protected Tag mapDtoToEntity(TagDto dto) {
		Tag tag = tagService.findById(dto.getId(), true);

		if (tag == null) {
			if (dto.getId() != null) {
				return null;
			}
			tag = new Tag();
		} else {
			if (tag.getDeleted())
				throw new RuntimeException("can't do operation on deleted tag");
		}

		if (dto.getName() != null) {
			tag.setName(dto.getName());
		}
		
		if (dto.getMessage() != null) {
			tag.setMessage(dto.getMessage());
		}

		/*
		 * if (dto.getLayerDtos() != null) {
		 * tag.setLayers(layerMapper.dtosToEntitys(dto.getLayerDtos())); }
		 * 
		 * if (dto.getMapDtos() != null) {
		 * tag.setMaps(mapMapper.dtosToEntitys(dto.getMapDtos())); }
		 * 
		 * if (dto.getEntityElementDtos() != null) {
		 * tag.setEntityElements(entityElementMapper.dtosToEntitys(dto.
		 * getEntityElementDtos())); }
		 */

		return tag;
	}
}
