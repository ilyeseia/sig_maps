package dz.eadn.sig.mapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.LayerSimpleDto;
import dz.eadn.sig.dto.MapLayerDto;
import dz.eadn.sig.dto.MapSimpleDto;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.Map;
import dz.eadn.sig.model.MapLayer;
import dz.eadn.sig.repository.MapLayerRepository;

/**
 * @author A.LAMOUR
 *
 */
@Component
public class MapLayerMapper {

	@Autowired
	private MapLayerRepository mapLayerRepository;

	@Autowired
	private ModelMapper modelMapper;

	public MapLayerDto entityToDto(MapLayer entity) {
		MapLayerDto mapLayerDto = new MapLayerDto();

		LayerSimpleDto layerSimpleDto = (modelMapper.map(entity.getLayer(), LayerSimpleDto.class));

		mapLayerDto.setMap(modelMapper.map(entity.getMap(), MapSimpleDto.class));
		mapLayerDto.setLayer(layerSimpleDto);
		return mapLayerDto;
	}

	public MapLayer dtoToEntity(MapLayerDto dto) {
		MapLayer mapLayer = null;
		if (dto.getId() != null) {
			Optional<MapLayer> optional = mapLayerRepository.findById(dto.getId());
			if (optional.isPresent())
				mapLayer = optional.get();
		} else
			mapLayer = new MapLayer();

		if (dto.getMap() != null) {
			mapLayer.setMap(modelMapper.map(dto.getMap(), Map.class));
		}

		if (dto.getLayer() != null)
			mapLayer.setLayer(modelMapper.map(dto.getLayer(), Layer.class));

		mapLayer.setOrder(dto.getOrder());
		mapLayer.setIsVisible(dto.getIsVisible());

		return mapLayer;
	}

	public List<MapLayer> dtosToEntitys(List<MapLayerDto> dtos) {
		return dtos.stream().map(dto -> dtoToEntity(dto)).filter(e -> e != null).collect(Collectors.toList());
	}

	public List<MapLayerDto> entitysToDtos(List<MapLayer> entitys) {
		return entitys.stream().map(dto -> entityToDto(dto)).filter(dto -> dto != null).collect(Collectors.toList());
	}
}
