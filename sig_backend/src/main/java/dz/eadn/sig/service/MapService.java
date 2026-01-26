
package dz.eadn.sig.service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import dz.eadn.sig.dto.*;
import org.geotools.geometry.jts.ReferencedEnvelope;

import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.Map;
import dz.eadn.sig.service.common.CommonService;

/**
 * @author Achrouf Abdenour & Ameur LAMOUR
 *
 */
public interface MapService extends CommonService<Map, MapDto> {
	public PageDto<MapSimpleDto> getAllPublicMaps(Integer page, Integer limit);

	public ReferencedEnvelope getMapBBOX(Map map);

	public void addRule(String layerSlug);

	public MapSimpleDto shareMap(UUID id, ShareMapWithOthers shareMap);

	public PageDto<UserSimpleDto> getUsersSharingMap(Map map, Integer page, Integer limit, String sort, String dir);

	public PageDto<GroupSimpleDto> getGroupsSharingMap(Map map, Integer page, Integer limit, String sort, String dir);

	public MapSimpleWithOthersDto getMapSharedWithOthers(UUID id, String source, Integer page, Integer limit,
			String sort, String dir);

	public PageDto<MapSimpleDto> getMapsByPage(Integer page, Integer limit, String sort, String dir);

	public PageDto<MapSimpleDto> findMapsByFilter(CommonFilter filter, Integer page, Integer limit, String sort,
			String dir);

	public PageDto<MapSimpleDto> getAllPublicMaps(Integer page, Integer limit, String name);

	public MapSimpleDto archiveMap(MapDto mapDto, UUID id);

	public List<UUID> getLayersInMap(UUID mapId);

	public List<HashMap<String, String>> getAllLayersMap(UUID mapId, boolean isPublic);

	// public List<LayerSimpleWithFieldsDto> getLayersSimpleWithFieldsInMap(UUID
	// mapId);

	public List<LayerSimpleWithFieldsDto> getLayersSimpleWithFields(UUID mapId, boolean authenticated);

	public List<LayerSimpleWithFieldsAndResourcesDto> getLayersSimpleWithFieldsAndResources(UUID mapId);

	public MapSimpleDto saveMap(MapDto mapDto, NotificationSimpleDto notificationSimpleDto);

	public MapSimpleDto createMap(MapDto mapDto);

	public MapSimpleDto updateMap(MapDto mapDto);

	public MapSimpleDto cloneMap(String mapSlug, CloneMapDto cloneMapDto);

	public String buildLayersMapQuery();

	public Boolean checkIfThemeContainLayer(UUID themeId, UUID layerId);

}
