package dz.eadn.sig.mapper;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import dz.eadn.sig.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.LayerDto;
import dz.eadn.sig.model.Field;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.service.LayerService;

/**
 * @author Achrouf Abdenour
 *
 */

@Component
@Primary
public class LayerMapper extends CommonMapper<Layer, LayerDto> {

	@Autowired
	private LayerService layerService;

	@Autowired
	private FieldMapper fieldMapper;

	@Autowired
	private TagMapper tagMapper;

	@Autowired
	private UserMapper userMapper;

	@Override
	protected LayerDto mapEntityToDto(Layer entity) {
		LayerDto layerDto = new LayerDto();
		layerDto.setName(entity.getName());
		layerDto.setTopo(entity.getTopo());
		layerDto.setTypeLimit(entity.getTypeLimit());
		layerDto.setSlug(entity.getSlug());
		layerDto.setType(entity.getType());
		/*
		 * if (entity.getGroups() != null)
		 * layerDto.setGroupDtos(groupMapper.entitysToDtos(entity.getGroups()));
		 * 
		 * if (entity.getUsers() != null)
		 * layerDto.setUserDtos(userMapper.entitysToDtos(entity.getUsers()));
		 */
		if (entity.getIdentifiant() != null)
			layerDto.setIdentifiant(entity.getIdentifiant());


		if (entity.getFields() != null)
			layerDto.setFieldDtos(fieldMapper.entitysToDtos(entity.getFields()));

		if (entity.getTags() != null) {
			layerDto.setTagDtos(tagMapper.entitysToDtos(entity.getTags()));
		}

		return layerDto;
	}

	@Override
	protected Layer mapDtoToEntity(LayerDto dto) {
		Layer layer = layerService.findById(dto.getId(), true);

		if (layer == null) {
			if (dto.getId() != null) {
				return null;
			}

			layer = new Layer();
		} else {
			if (layer.getDeleted())
				throw new RuntimeException("can't do operation on deleted layer");
		}

		if (dto.getIdentifiant() != null) {
			layer.setIdentifiant(dto.getIdentifiant());
		}

		if (dto.getName() != null) {
			layer.setName(dto.getName());
		}


		if (dto.getTopo() != null) {
			layer.setTopo(dto.getTopo());
		}


		if (dto.getSlug() != null) {
			layer.setSlug(dto.getSlug());
		}

		if (dto.getTypeLimit() != null) {
			layer.setTypeLimit(dto.getTypeLimit());
		}

		if (dto.getDeleted() != null) {
			layer.setDeleted(dto.getDeleted());
		}

		if (dto.getType() != null) {
			layer.setType(dto.getType());
		}


		/*
		 * if (dto.getGroupDtos() != null)
		 * layer.setGroups(groupMapper.dtosToEntitys(dto.getGroupDtos()));
		 * 
		 * if (dto.getUserDtos() != null)
		 * layer.setUsers(userMapper.dtosToEntitys(dto.getUserDtos()));
		 */

		/*
		 * if (dto.getTagDtos() != null) { List<Tag> tags =
		 * tagMapper.dtosToEntitys(dto.getTagDtos()); tags.stream().filter(tag ->
		 * tag.getId() == null).forEach(tag -> tag = tagService.save(tag));
		 * 
		 * layer.setTags(tags); }
		 */


		if (dto.getFieldDtos() != null) {

			List<Field> newFields = fieldMapper.dtosToEntitys(dto.getFieldDtos());

			if (layer.getFields() != null) {
				layer.getFields().clear();
			}
			final Layer l = layer;
			newFields.stream().filter(field -> field.getLayer() == null).forEach(field -> {
				field.setLayer(l);
			});
			layer.getFields().addAll(newFields);
		}

		if(dto.getUserDtos() != null){
			List<User> userList = userMapper.dtosToEntitys(dto.getUserDtos());

			layer.getUsers().addAll(userList);
		}

		return layer;
	}
}
