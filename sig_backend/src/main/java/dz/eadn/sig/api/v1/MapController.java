
package dz.eadn.sig.api.v1;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import dz.eadn.sig.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dz.eadn.sig.controller.common.CommonController;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.mapper.MapMapper;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.Map;
import dz.eadn.sig.model.Privacy;
import dz.eadn.sig.service.MapLayerService;
import dz.eadn.sig.service.MapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour && LAMOUR Ameur
 *
 */
@RestController
@Slf4j
@RequestMapping("/api/v1.0/maps")
public class MapController extends CommonController<Map, MapDto> {

	@Autowired
	private MapService mapService;

	@Autowired
	private MapMapper mapper;

	@Autowired
	private MapLayerService mapLayerService;

	public MapController() {
		super(Map.class);
	}

	@Operation(summary = "Create a map", description = "add a single map to databse", tags = { "map" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Map created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
			@ApiResponse(responseCode = "409", description = "Map already exist"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('MAP_CREATE_AUTHORITY')")
	@PostMapping
	public ResponseEntity<?> create(
			@Parameter(description = "Map to add. Cannot null or empty.", required = true, schema = @Schema(implementation = Map.class)) @Valid @RequestBody MapDto dto,
			BindingResult results) {

		MapSimpleDto mapSimpleDto = mapService.createMap(dto);
		return new ResponseEntity<MapSimpleDto>(mapSimpleDto, HttpStatus.OK);

	}

	@Operation(summary = "Find map by uuid", description = "Provide an uuid to look up a specific map from database", tags = {
			"map" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieve one map", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
			@ApiResponse(responseCode = "404", description = "Map not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('MAP_READ_AUTHORITY')")
	@GetMapping("/{uuid}")
	public ResponseEntity<?> find(
			@Parameter(description = "uuid of the map to be obtained. Cannot be empty.", required = true) @PathVariable("uuid") UUID uuid)
			throws EntityNotFoundException {
		return super.find(uuid);
	}

	@Operation(summary = "Find all resources ", description = "Look up all resources from database", tags = { "map" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all resources", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Map.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('MAP_READ_AUTHORITY')")
	@GetMapping
	public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir) {

		PageDto<MapSimpleDto> mapTableDtoPage = mapService.getMapsByPage(page, limit, sort, dir);
		return new ResponseEntity<PageDto<MapSimpleDto>>(mapTableDtoPage, HttpStatus.OK);

	}

	@Operation(summary = "Find all layers by criteria ", description = "Look up all layers from database", tags = {
			"Layer" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all layers", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Layer.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('MAP_READ_AUTHORITY')")
	@PostMapping("/search")
	public ResponseEntity<?> findAllByCriteria(
			@Parameter(description = "Provide a payload how has the attributes of layer", required = true) @RequestBody CommonFilter filter,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {

		PageDto<MapSimpleDto> mapTableDtoPage = mapService.findMapsByFilter(filter, page, limit, sort, dir);
		return new ResponseEntity<PageDto<MapSimpleDto>>(mapTableDtoPage, HttpStatus.OK);
	}

	@Operation(summary = "Update a map", description = "Provide an uuid to look up a specific map from database to updated", tags = {
			"map" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Update one map", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
			@ApiResponse(responseCode = "404", description = "Map not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('MAP_UPDATE_AUTHORITY')")
	@PutMapping("/{uuid}")
	public ResponseEntity<?> update(
			@Parameter(description = "uuid of the map to be updated. Cannot be empty.", required = true) @PathVariable("uuid") UUID id,
			@Parameter(description = "Map to add. Cannot null or empty.", required = true, schema = @Schema(implementation = MapDto.class)) @Valid @RequestBody MapDto dto,
			BindingResult results,
			@RequestParam(value = "image", required = false, defaultValue = "false") boolean updateImage) {

		log.info("Updating {} with uuid {}", domainClass.getSimpleName(), id);
		String error = "Impossible de mUettre � jour " + domainClass.getSimpleName() + ", il n'exste pas !!";
		MapSimpleDto mapSimpleDto;
		try {
			dto.setId(id);
			mapSimpleDto = mapService.updateMap(dto);
		} catch (Exception e) {
			log.error(error + ":" + e.getMessage());
			return new ResponseEntity<String>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<MapSimpleDto>(mapSimpleDto, HttpStatus.ACCEPTED);

	}

	@Operation(summary = "share a map", description = "Provide an uuid to share map", tags = { "map" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Share a map", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
			@ApiResponse(responseCode = "404", description = "Map not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('MAP_SHARE_AUTHORITY')")
	@PostMapping("/share/{uuid}")
	public ResponseEntity<?> shareMap(
			@Parameter(description = "uuid of the map to be updated. Cannot be empty.", required = true) @PathVariable("uuid") UUID id,
			@Parameter(description = "Map to add. Cannot null or empty.", required = true, schema = @Schema(implementation = MapDto.class)) @RequestBody ShareMapWithOthers sharedMap) {

		MapSimpleDto mapTableDto = mapService.shareMap(id, sharedMap);
		return new ResponseEntity<MapSimpleDto>(mapTableDto, HttpStatus.OK);

		/*
		 * Authentication authentication =
		 * SecurityContextHolder.getContext().getAuthentication();
		 * 
		 * Set<String> users = result.getUserDtos().stream().map(user ->
		 * user.getUsername()).collect(Collectors.toSet()); for (SessionDto session :
		 * redisUtil.findAllSessions()) { if
		 * (!session.getUserName().equals(authentication.getName()) &&
		 * users.contains(session.getUserName())) {
		 * messagingTemplate.convertAndSendToUser(session.getToken(),
		 * "/notification/item", result); } }
		 */

	}

	@Operation(summary = "Delete a map", description = "Provide an uuid to look up a specific map from database to deleted", tags = {
			"map" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Delete one map", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
			@ApiResponse(responseCode = "404", description = "Map not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('MAP_DELETE_AUTHORITY')")
	@DeleteMapping("/{uuid}")
	public ResponseEntity<?> delete(
			@Parameter(description = "uuid of the map to be deleted. Cannot be empty.", required = true) @PathVariable("uuid") UUID uuid) {
		return super.delete(uuid);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('MAP_READ_AUTHORITY')")
	@GetMapping("/count")
	public ResponseEntity<?> count() {
		return super.count();
	}

	@GetMapping("/public")
	public ResponseEntity<?> getAllPublicMaps(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit) {
		try {
			PageDto<MapSimpleDto> dtos = mapService.getAllPublicMaps(page, limit);
			return new ResponseEntity<PageDto<MapSimpleDto>>(dtos, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/public/filter")
	public ResponseEntity<?> getAllPublicMapsByFilter(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam String name) {
		try {
			PageDto<MapSimpleDto> dtos = mapService.getAllPublicMaps(page, limit, name);
			return new ResponseEntity<PageDto<MapSimpleDto>>(dtos, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/public/{uuid}")
	public ResponseEntity<?> getMapWithPublicLink(@PathVariable("uuid") UUID id) {
		Map map = mapService.findById(id);
		if (map == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		if (map.getPrivacy().equals(Privacy.PUBLIC_WITH_LINK)) {
			MapDto dto = mapper.entityToDto(map);
			return new ResponseEntity<MapDto>(dto, HttpStatus.OK);
		} /* Allow Public Map to share Position */
		else if (map.getPrivacy().equals(Privacy.PUBLIC)) {
			MapDto dto = mapper.entityToDto(map);
			return new ResponseEntity<MapDto>(dto, HttpStatus.OK);
		} /* Allow PRIVATE Map to share Position */
		else if (map.getPrivacy().equals(Privacy.PRIVATE)) {
			MapDto dto = mapper.entityToDto(map);
			return new ResponseEntity<MapDto>(dto, HttpStatus.OK);
		}

		else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}

	@Operation(summary = "archive a map", description = "Provide an uuid to archive map", tags = { "map" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Archive a map", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
			@ApiResponse(responseCode = "404", description = "Map not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('MAP_ARCHIVE_AUTHORITY')")
	@PostMapping("/archive/{uuid}")
	public ResponseEntity<?> archiveMap(@RequestBody MapDto mapDto, @PathVariable("uuid") UUID id) {

		MapSimpleDto result = mapService.archiveMap(mapDto, id);
		return new ResponseEntity<MapSimpleDto>(result, HttpStatus.OK);

	}

	/*
	 * @Operation(summary = "set layers tp the map", description =
	 * "Provide mapLayer object", tags = { "mapLayer" })
	 * 
	 * @ApiResponses(value = {
	 * 
	 * @ApiResponse(responseCode = "200", description = "Archive a map", content
	 * = @Content(mediaType = "application/json", schema = @Schema(implementation =
	 * MapLayer.class))),
	 * 
	 * @ApiResponse(responseCode = "404", description = "Map not found"),
	 * 
	 * @ApiResponse(responseCode = "500", description = "Internal Server Error") })
	 */

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('ATTACH_LAYER_MAP_AUTHORITY')")
	@PostMapping("/attach")
	public ResponseEntity<?> attachLayersToMap(@RequestBody List<MapLayerDto> mapLayerDtos) {

		List<LayerSimpleWithFieldsDto> withFieldsAndResourceDtos = mapLayerService.attachLayersToMap(mapLayerDtos, true);
		return new ResponseEntity<List<LayerSimpleWithFieldsDto>>(withFieldsAndResourceDtos, HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('DETACH_LAYER_MAP_AUTHORITY')")
	@PostMapping("/detach/{mapId}/{layerId}")
	public ResponseEntity<?> detachLayersFromMap(@RequestBody List<MapLayerDto> mapLayerDtos, @PathVariable UUID mapId,
			@PathVariable UUID layerId) {

		List<LayerSimpleWithFieldsDto> withFieldsAndResourceDtos = mapLayerService.detachLayersFromMap(mapLayerDtos,
				mapId, layerId);
		return new ResponseEntity<List<LayerSimpleWithFieldsDto>>(withFieldsAndResourceDtos, HttpStatus.OK);

	}

	@Operation(summary = "sort layers inside map", description = "Provide an uuid to look up a specific layer from database to updated", tags = {
			"Layer" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "layers sorted", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Layer.class))),
			@ApiResponse(responseCode = "404", description = "Layer not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('MAP_UPDATE_AUTHORITY')")
	@PutMapping("/sort")
	public ResponseEntity<?> sortLayers(@RequestBody List<MapLayerDto> mapLayerDtos) {
		mapLayerService.sortLayers(mapLayerDtos);
		return null;
	}

	/*
	 * @Operation(summary = "get users hows share a same map", description =
	 * "Provide an uuid to get map", tags = { "map" })
	 * 
	 * @ApiResponses(value = {
	 * 
	 * @ApiResponse(responseCode = "200", description = "Share a map", content
	 * = @Content(mediaType = "application/json", schema = @Schema(implementation =
	 * Map.class))),
	 * 
	 * @ApiResponse(responseCode = "404", description = "Map not found"),
	 * 
	 * @ApiResponse(responseCode = "500", description = "Internal Server Error") })
	 * 
	 * @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('MAP_SHARE_AUTHORITY')")
	 * 
	 * @GetMapping("/sharedWithUsers/{uuid}") public ResponseEntity<?>
	 * getSharedMapWithUsers(@RequestParam(defaultValue = "0") Integer page,
	 * 
	 * @RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue
	 * = "username") String sort,
	 * 
	 * @RequestParam(defaultValue = "desc") String dir,
	 * 
	 * @Parameter(description = "uuid of the map to be updated. Cannot be empty.",
	 * required = true) @PathVariable("uuid") UUID id) {
	 * 
	 * PageDto<UserSimpleDto> pageDto = mapService.getUsersSharingMap(id, page,
	 * limit, sort, dir); return new ResponseEntity<PageDto<UserSimpleDto>>(pageDto,
	 * HttpStatus.OK);
	 * 
	 * }
	 * 
	 * @Operation(summary = "get groups hows share a same map", description =
	 * "Provide an uuid to get map", tags = { "map" })
	 * 
	 * @ApiResponses(value = {
	 * 
	 * @ApiResponse(responseCode = "200", description = "Share a map", content
	 * = @Content(mediaType = "application/json", schema = @Schema(implementation =
	 * Map.class))),
	 * 
	 * @ApiResponse(responseCode = "404", description = "Map not found"),
	 * 
	 * @ApiResponse(responseCode = "500", description = "Internal Server Error") })
	 * 
	 * @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('MAP_SHARE_AUTHORITY')")
	 * 
	 * @GetMapping("/sharedWithGroups/{mapId}") public ResponseEntity<?>
	 * getSharedMapWithGroups(@RequestParam(defaultValue = "0") Integer page,
	 * 
	 * @RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue
	 * = "name") String sort,
	 * 
	 * @RequestParam(defaultValue = "desc") String dir,
	 * 
	 * @Parameter(description = "uuid of the map to be updated. Cannot be empty.",
	 * required = true) @PathVariable("mapId") UUID id) {
	 * 
	 * PageDto<GroupSimpleDto> pageDto = mapService.getGroupsSharingMap(id, page,
	 * limit, sort, dir); return new
	 * ResponseEntity<PageDto<GroupSimpleDto>>(pageDto, HttpStatus.OK);
	 * 
	 * }
	 */

	@Operation(summary = "get groups hows share a same map", description = "Provide an uuid to get map", tags = {
			"map" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Share a map", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
			@ApiResponse(responseCode = "404", description = "Map not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('MAP_SHARE_AUTHORITY')")
	@GetMapping("/mapSharedWithOthers/{source}/{uuid}")
	public ResponseEntity<?> getMapSharedWithOthers(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir,
			@Parameter(description = "uuid of the map to be updated. Cannot be empty.", required = true) @PathVariable("source") String source,
			@PathVariable("uuid") UUID id) {

		MapSimpleWithOthersDto mapWithOthers = mapService.getMapSharedWithOthers(id, source, page, limit, sort, dir);
		return new ResponseEntity<MapSimpleWithOthersDto>(mapWithOthers, HttpStatus.OK);

	}

	@Operation(summary = "get groups hows share a same map", description = "Provide an uuid to get map", tags = {
			"map" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Share a map", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
			@ApiResponse(responseCode = "404", description = "Map not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('MAP_READ_AUTHORITY')")
	@GetMapping("/layersWithFields/{mapId}")
	public ResponseEntity<?> getLayersSimpleWithFields(
			@Parameter(description = "uuid of the map to be updated. Cannot be empty.", required = true) @PathVariable("mapId") UUID id) {

		List<LayerSimpleWithFieldsDto> layerSimpleWithFieldsDto = mapService.getLayersSimpleWithFields(id, true);
		return new ResponseEntity<List<LayerSimpleWithFieldsDto>>(layerSimpleWithFieldsDto, HttpStatus.OK);

	}

	@GetMapping("/public/{map-id}/layers-styles")
	public ResponseEntity<?> getLayersInPublicMap(@PathVariable("map-id") UUID mapId) {
		return new ResponseEntity<List<HashMap<String, String>>>(mapService.getAllLayersMap(mapId, true), HttpStatus.OK);
	}

	@GetMapping("/{map-id}/layers-styles")
	public ResponseEntity<?> getLayersInMap(@PathVariable("map-id") UUID mapId) {
		return new ResponseEntity<List<HashMap<String, String>>>(mapService.getAllLayersMap(mapId, false), HttpStatus.OK);
	}

	@Operation(summary = "get groups hows share a same map", description = "Provide an uuid to get map", tags = {
			"map" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Share a map", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
			@ApiResponse(responseCode = "404", description = "Map not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@GetMapping("/public/layersWithFields/{mapId}")
	public ResponseEntity<?> getPublicLayersSimpleWithFields(
			@Parameter(description = "uuid of the map to be updated. Cannot be empty.", required = true) @PathVariable("mapId") UUID id) {

		List<LayerSimpleWithFieldsDto> layerSimpleWithFieldsDto = mapService.getLayersSimpleWithFields(id, false);
		return new ResponseEntity<List<LayerSimpleWithFieldsDto>>(layerSimpleWithFieldsDto, HttpStatus.OK);

	}

	@Operation(summary = "get groups hows share a same map", description = "Provide an uuid to get map", tags = {
			"map" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Share a map", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
			@ApiResponse(responseCode = "404", description = "Map not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('MAP_READ_AUTHORITY')")
	@GetMapping("/layersWithFieldsAndResources/{uuid}")
	public ResponseEntity<?> getLayersSimpleWithFieldsAndResources(
			@Parameter(description = "uuid of the map to be updated. Cannot be empty.", required = true) @PathVariable("uuid") UUID id) {

		List<LayerSimpleWithFieldsAndResourcesDto> withFieldsAndResourcesDtos = mapService
				.getLayersSimpleWithFieldsAndResources(id);
		return new ResponseEntity<List<LayerSimpleWithFieldsAndResourcesDto>>(withFieldsAndResourcesDtos,
				HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('MAP_CLONE_AUTHORITY')")
	@PostMapping("/{slug}/clone")
	public ResponseEntity<?> cloneLayer(@PathVariable("slug") String layerSlug, @RequestBody CloneMapDto cloneMapDto) {
		return new ResponseEntity<MapSimpleDto>(mapService.cloneMap(layerSlug, cloneMapDto), HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('MAP_CLONE_AUTHORITY')")
	@GetMapping("/set-visibility")
	public void setLayerVisibility(@RequestParam("map-layer-id")UUID mapLayerId, @RequestParam("visibility") Boolean visibility) {
		mapLayerService.updateLayerVisibility(mapLayerId, visibility);
	}
}
