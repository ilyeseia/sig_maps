package dz.eadn.sig.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.LayerDto;
import dz.eadn.sig.dto.MapDto;
import dz.eadn.sig.model.Map;
import dz.eadn.sig.service.MapService;

/**
 * @author Achrouf Abdenour
 *
 */
@Component
public class MapMapper extends CommonMapper<Map, MapDto> {

	@Autowired
	private MapService mapService;

	@Autowired
	private GroupMapper groupMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private TagMapper tagMapper;

	@Autowired
	private CommonModelMapper<?, ?> cModelMapper;

	@Override
	protected MapDto mapEntityToDto(Map entity) {

		MapDto mapDto = new MapDto();
		mapDto.setName(entity.getName());
		mapDto.setSlug(entity.getSlug());
		mapDto.setImage(entity.getImage());
		mapDto.setLayerDtos(null);

		List<LayerDto> layerDtos = cModelMapper.mapList(
				entity.getLayers().stream().map(ml -> ml.getLayer()).collect(Collectors.toList()), LayerDto.class);
		mapDto.setLayerDtos(layerDtos);
		// mapDto.setLayerDtos(mapLayerMapper.entitysToDtos(entity.getLayers()));

		/*
		 * Set<Layer> layers = entity.getLayers();
		 * 
		 * layers.sort(new Comparator<Layer>() {
		 * 
		 * @Override public int compare(Layer o1, Layer o2) { return (int)
		 * (o2.getOrder() - o1.getOrder()); } });
		 * mapDto.setLayerDtos(layerMapper.entitysToDtos(layers));
		 */

		if (entity.getGroups() != null)
			mapDto.setGroupDtos(groupMapper.entitysToDtos(entity.getGroups()));

		if (entity.getUsers() != null)
			mapDto.setUserDtos(userMapper.entitysToDtos(entity.getUsers()));

		if (entity.getTags() != null)
			mapDto.setTagDtos(tagMapper.entitysToDtos(entity.getTags()));

		if (entity.getPrivacy() != null)
			mapDto.setPrivacy(entity.getPrivacy());

		return mapDto;
	}

	@Override
	protected Map mapDtoToEntity(MapDto dto) {
		Map map = mapService.findById(dto.getId(), true);

		if (map == null) {
			if (dto.getId() != null) {
				return null;
			}
			map = new Map();
		} else {
			if (map.getDeleted())
				throw new RuntimeException("can't do operation on deleted map");
		}

		if (dto.getName() != null) {
			map.setName(dto.getName());
		}

		if (dto.getImage() != null) {
			map.setImage(dto.getImage());
		}

		if (dto.getSlug() != null) {
			map.setSlug(dto.getSlug());
		}

		if (dto.getPrivacy() != null) {
			map.setPrivacy(dto.getPrivacy());
		}

		/*
		 * if (dto.getLayerDtos() != null) {
		 * map.setLayers(mapLayerMapper.dtosToEntitys(dto.getLayerDtos())); }
		 */

		/*
		 * if (dto.getTagDtos() != null) { List<Tag> tags =
		 * tagMapper.dtosToEntitys(dto.getTagDtos()); tags.stream().filter(tag ->
		 * tag.getId() == null).forEach(tag -> tag = tagService.save(tag));
		 * 
		 * map.setTags(tags); }
		 */

		if (dto.getGroupDtos() != null)
			map.setGroups(groupMapper.dtosToEntitys(dto.getGroupDtos()));

		if (dto.getUserDtos() != null)
			map.setUsers(userMapper.dtosToEntitys(dto.getUserDtos()));

		return map;
	}
}
