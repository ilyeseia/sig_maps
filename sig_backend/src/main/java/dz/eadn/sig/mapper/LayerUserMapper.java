package dz.eadn.sig.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.LayerDto;
import dz.eadn.sig.model.Layer;

/**
 * @author Ameur LAMOUR
 *
 */

@Component
@Qualifier("layerUserMapper")
public class LayerUserMapper extends CommonMapper<Layer, LayerDto> {

	@Autowired
	private FieldMapper fieldMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private GroupMapper groupMapper;


	@Override
	protected LayerDto mapEntityToDto(Layer entity) {
		LayerDto layerDto = new LayerDto();

		layerDto.setName(entity.getName());
		layerDto.setTopo(entity.getTopo());
		layerDto.setTypeLimit(entity.getTypeLimit());
		layerDto.setSlug(entity.getSlug());
		layerDto.setType(entity.getType());

		if (entity.getGroups() != null)
			layerDto.setGroupDtos(groupMapper.entitysToDtos(entity.getGroups()));

		if (entity.getUsers() != null)
			layerDto.setUserDtos(userMapper.entitysToDtos(entity.getUsers()));

		if (entity.getIdentifiant() != null)
			layerDto.setIdentifiant(entity.getIdentifiant());


		if (entity.getFields() != null)
			layerDto.setFieldDtos(fieldMapper.entitysToDtos(entity.getFields()));


		return layerDto;
	}

	@Override
	protected Layer mapDtoToEntity(LayerDto dto) {
		return null;
	}

}
