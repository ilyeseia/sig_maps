package dz.eadn.sig.service;

import java.util.List;
import java.util.UUID;

import dz.eadn.sig.dto.LayerSimpleWithFieldsDto;
import dz.eadn.sig.dto.LayerStyleSimpleDto;
import dz.eadn.sig.dto.MapLayerDto;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.Map;
import dz.eadn.sig.model.MapLayer;
import dz.eadn.sig.model.Style;

public interface MapLayerService {

	List<LayerSimpleWithFieldsDto> saveAll(List<MapLayerDto> mapLayerDtos, Boolean superficialClone);

	List<LayerSimpleWithFieldsDto> attachLayersToMap(List<MapLayerDto> mapLayerDtos, boolean updatePrivacy);

	List<LayerSimpleWithFieldsDto> detachLayersFromMap(List<MapLayerDto> mapLayerDtos, UUID mapId, UUID layerId);

	void sortLayers(List<MapLayerDto> mapLayerDtos);

	List<Style> getAllLayerStyles(Layer layer);

	List<Style> getAllMapStyles(Map map);

	List<LayerStyleSimpleDto> initializeStyle(List<MapLayerDto> mapLayerDtos, UUID mapId, Layer layer, MapLayer mapLayer, Boolean superficialClone) throws Exception;

	void removeById(UUID mapId, UUID layerId);

	void updateLayerVisibility(UUID mapLayerId,Boolean visibility);

	void updateLayerOrder(int order, UUID mapLayerId);

}
