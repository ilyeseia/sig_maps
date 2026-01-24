package dz.eadn.sig.service;


import dz.eadn.sig.dto.LayerStyleDto;
import dz.eadn.sig.dto.LayerStyleSimpleDto;
import dz.eadn.sig.model.*;
import dz.eadn.sig.service.common.CommonService;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

/**
 * @author Chouaib LOKBANI
 *
 */
public interface LayerStylesService extends CommonService<Style, LayerStyleDto> {

    LayerStyleSimpleDto createLayerStyle(LayerStyleDto layerStyleDto, MapLayer mapLayer, Theme theme);

    ResponseEntity updateLayerStyle(LayerStyleDto layerStyleDto);

    LayerStyleSimpleDto updateLayerStyleName(LayerStyleDto layerStyleDto);

    void deleteLayerStyle(UUID layerStyleId, Boolean deleteFromDB);

    ResponseEntity<UUID> deleteLayerStyle(UUID layerStyleId, Boolean deleteFromDB, Boolean sendNotifications);

    void deleteLayerStyle(Style style, Boolean deleteFromDB);

    void deleteStylesByLayer(Layer layer);

    void deleteStylesByMap(Map map);

    void setDefaultLayerStyle(UUID mapLayerId, UUID styleId);

    List<LayerStyleSimpleDto> getDefaultStyleInLayerMap(List<UUID> mapLayers);

    List<LayerStyleSimpleDto> getLayerStylesInTheme(String layerSlug, UUID mapLayerId);

    List<LayerStyleDto> getLayerStylesInTheme(UUID mapLayerId);

    String generateStyleName(String name, String mapId, String layerId, String styleId);

    LayerStyleSimpleDto cloneLayerStyle(LayerStyleDto style);

}

























