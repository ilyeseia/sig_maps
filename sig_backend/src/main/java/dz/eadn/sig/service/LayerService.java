package dz.eadn.sig.service;

import java.util.List;
import java.util.UUID;

import dz.eadn.sig.dto.*;
import dz.eadn.sig.util.SearchCriteria;
import org.springframework.http.ResponseEntity;

import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.TypeLimit;
import dz.eadn.sig.service.common.CommonService;

/**
 * @author Achrouf Abdenour & LAMOUR Ameur & LOKBANI Chouaib
 *
 */
public interface LayerService extends CommonService<Layer, LayerDto> {

	public void setForceDelete(boolean value);

	public LayerDto createLayer(LayerDto layerDto);

	public LayerDto updateLayer(LayerDto layerDto);

	public Layer findBySlug(String slug);

	public Layer findById(UUID id);

	public List<Layer> findAll();

	List<LayerProjection> findAllBYSlug();

	public void createSqlView(Layer layer);

	public void shareLayer(UUID id, ShareLayerWithOthers sharedLayer);

	public PageDto<LayerSimpleDto> findAllLayersByPage(Integer page, Integer limit, String sort, String dir);

	public PageDto<UserSimpleDto> getUsersSharingLayer(Layer layer, Integer page, Integer limit, String sort,
			String dir);

	public PageDto<UserCompleteDto> getUsersSharingLayerAutoComplete(String layerSlug, String name, Integer page,
			Integer limit, String sort, String dir);

	public PageDto<GroupSimpleDto> getGroupsSharingLayer(Layer layer, Integer page, Integer limit, String sort,
			String dir);

	public LayerSimpleWithOthersDto getLayerWithOthers(UUID id, String source, Integer page, Integer limit, String sort,
			String dir);

	PageDto<LayerSimpleDto> findAllLayersByFilter(CommonFilter filter, Integer page, Integer limit, String sort,
			String dir);

	PageDto<LayerSimpleDto> findAllLayersByFilterInMap(CommonFilter filter, UUID mapId, Integer page, Integer limit,
			String sort, String dir);

	PageDto<LayerSimpleWithFieldsDto> findAllLayersSharedInMap(UUID mapId, Integer page, Integer limit, String sort,
			String dir);

	PageDto<LayerSimpleWithFieldsDto> findAllSharedLayers(UUID mapId, Integer page, Integer limit, String sort,
			String dir);

	LayerSimpleWithFieldsDto getLayerWithFields(UUID id);

	LayerSimpleWithFieldsAndResourcesDto getLayerWithFieldsAndResources(UUID id, String mapSlug, String mode,
			boolean authenticated);

//	String getStyle(String layerSlug, String mapSlug);

	boolean CheckIfUserHasPrivilegeOnLayer(String layerSlug, String mapSlug, String permission, String mode);

	boolean CheckIfUserHasPrivilegeOnLayerAndEntityElement(String layerSlug, String mapSlug, String permission, String mode, UUID entityElementId);

	List<ButtonPermission> CheckIfUserHasPrivilegeOnLayerAndEntityElements(List<ButtonPermission> buttonPermissions);

	boolean checkIfMapSharedWithUser(UUID mapId);

	List<LayerSimpleDto> findByTypeLimit(TypeLimit typeLimit);

	String generateQuery(String fieldType, SearchCriteria criteria, boolean fullQuery);

	LayerDto cloneLayer(String layerSlug, CloneLayerDto cloneLayerDto);

	Boolean checkIfLayerHasData(UUID layerId);

}
