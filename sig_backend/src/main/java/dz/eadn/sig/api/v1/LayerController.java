package dz.eadn.sig.api.v1;

import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import dz.eadn.sig.dto.*;
import dz.eadn.sig.service.GeoProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import dz.eadn.sig.mapper.LayerMapper;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.TypeLimit;
import dz.eadn.sig.service.LayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour && LAMOUR Ameur && LOKBANI Chouaib
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/v1.0/layers")
@Tag(name = "layer", description = "the Layer API")
public class LayerController extends CommonController<Layer, LayerDto> {

	@Autowired
	private LayerService layerService;

	@Autowired
	private GeoProcessingService geoProcessingService;

	public LayerController(LayerService layerService, LayerMapper mapper) {
		super(Layer.class);
	}

	@Operation(summary = "Create a layer", description = "add a single layer to databse, A layer must at least contain one Field inside it", tags = {
			"Layer" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Layer created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Layer.class))),
			@ApiResponse(responseCode = "409", description = "Layer already exist"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('LAYER_CREATE_AUTHORITY')")
	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody LayerDto dto, BindingResult results) {
		return super.create(dto, results);
	}

//	@Operation(summary = "Find Layer by uuid", description = "Provide an uuid to look up a specific layer from database", tags = {
//			"Layer" })
//	@ApiResponses(value = {
//			@ApiResponse(responseCode = "200", description = "Retrieve one layer", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Layer.class))),
//			@ApiResponse(responseCode = "404", description = "Layer not found"),
//			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
//
//	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('LAYER_READ_AUTHORITY')")
//	@GetMapping("/{uuid}")
//	public ResponseEntity<?> find(@PathVariable("uuid") UUID uuid) throws EntityNotFoundException {
//		return super.find(uuid);
//	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('LAYER_READ_AUTHORITY')")
	@GetMapping
	public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir) {

		PageDto<LayerSimpleDto> layerSimpleDtos = layerService.findAllLayersByPage(page, limit, sort, dir);
		return new ResponseEntity<PageDto<LayerSimpleDto>>(layerSimpleDtos, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('LAYER_READ_AUTHORITY')")
	@GetMapping("/with-slug")
	public ResponseEntity<?> findAllBySlug() {
		return new ResponseEntity<List<LayerProjection>>(layerService.findAllBYSlug(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('LAYER_READ_AUTHORITY')")
	@GetMapping("/count")
	public ResponseEntity<?> count() {
		return super.count();
	}

	@Operation(summary = "Find all layers by criteria ", description = "Look up all layers from database", tags = {
			"Layer" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all layers", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Layer.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('LAYER_READ_AUTHORITY')")
	@PostMapping("/search")
	public ResponseEntity<?> findAllByCriteria(
			@Parameter(description = "Provide a payload how has the attributes of layer", required = true) @RequestBody CommonFilter filter,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {

		PageDto<LayerSimpleDto> layerSimpleDtos = layerService.findAllLayersByFilter(filter, page, limit, sort, dir);
		return new ResponseEntity<PageDto<LayerSimpleDto>>(layerSimpleDtos, HttpStatus.OK);
	}

	@Operation(summary = "Update a Layer", description = "Provide an uuid to look up a specific layer from database to updated", tags = {
			"Layer" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Update one layer", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Layer.class))),
			@ApiResponse(responseCode = "404", description = "Layer not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('LAYER_UPDATE_AUTHORITY')")
	@PutMapping("/{uuid}")
	public ResponseEntity<?> update(@PathVariable("uuid") UUID uuid, @Valid @RequestBody LayerDto layerDto,
			BindingResult results) {
		return super.update(uuid, layerDto, results);
	}

	@GetMapping("/check-write-permission/{layerSlug}/{permission}")
	public boolean getPermissionForWrite(@PathVariable String layerSlug, @PathVariable String permission) {
		return layerService.CheckIfUserHasPrivilegeOnLayer(layerSlug, null, permission, "write");
	}

	@GetMapping("/check-write-geometry-permission/{layerSlug}/{permission}")
	public boolean getPermissionForWriteWithGeometry(@PathVariable String layerSlug, @PathVariable String permission, @RequestParam("entity-element") UUID entityElement) {
		return layerService.CheckIfUserHasPrivilegeOnLayerAndEntityElement(layerSlug, null, permission, "write", entityElement);
	}

	@PostMapping("/check-write-geometry-permission")
	public ResponseEntity<?>  getPermissionsForWriteWithGeometry(@RequestBody List<ButtonPermission> buttonPermissions) {
		return new ResponseEntity<List<ButtonPermission>>(layerService.CheckIfUserHasPrivilegeOnLayerAndEntityElements(buttonPermissions), HttpStatus.OK);
	}

	@GetMapping("/check-read-permission/maps/{mapSlug}/{layerSlug}/{permission}")
	public boolean getPermissionForRead(@PathVariable String layerSlug, @PathVariable String mapSlug,
			@PathVariable String permission) {
		return layerService.CheckIfUserHasPrivilegeOnLayer(layerSlug, mapSlug, permission, "read");
	}

	@Operation(summary = "Delete a Layer", description = "Provide an uuid to look up a specific Layer from database to deleted", tags = {
			"Layer" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Delete one layer", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Layer.class))),
			@ApiResponse(responseCode = "404", description = "Layer not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('LAYER_DELETE_AUTHORITY')")
	@DeleteMapping("/{uuid}")
	public ResponseEntity<?> delete(@PathVariable("uuid") UUID uuid) {
		return super.delete(uuid);
	}

	@Operation(summary = "Create an sql view for the layer", description = "the view will be used to configure a layer on geoserver", tags = {
			"Layer" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Update one layer", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Layer.class))),
			@ApiResponse(responseCode = "404", description = "Layer not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('LAYER_CREATE_AUTHORITY')")
	@PostMapping("/view/{id}")
	public ResponseEntity<?> createSqlView(@PathVariable("id") UUID id) {
		try {
			Layer layer = layerService.findById(id);
			layerService.createSqlView(layer);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(summary = "Create a layer", description = "add a single layer to databse, A layer must at least contain one Field inside it", tags = {
			"Layer" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Layer created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Layer.class))),
			@ApiResponse(responseCode = "409", description = "Layer already exist"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('LAYER_SHARE_AUTHORITY')")
	@PostMapping("/share/{uuid}")
	public ResponseEntity<?> share(@PathVariable("uuid") UUID id, @RequestBody ShareLayerWithOthers sharedLayer) {
		layerService.shareLayer(id, sharedLayer);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Find LayerSimpleWithFieldsDto by uuid", description = "Provide an uuid to look up a specific layer from database", tags = {
			"Layer" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieve one layer", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Layer.class))),
			@ApiResponse(responseCode = "404", description = "Layer not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('LAYER_READ_AUTHORITY')")
	@GetMapping("/withFields/{uuid}")
	public ResponseEntity<?> findSimpleLayerWithFields(@PathVariable("uuid") UUID id) throws EntityNotFoundException {
		LayerSimpleWithFieldsDto WithFieldsDto = layerService.getLayerWithFields(id);
		return new ResponseEntity<LayerSimpleWithFieldsDto>(WithFieldsDto, HttpStatus.OK);
	}

	@Operation(summary = "Find LayerSimpleWithFieldsAndResourceDto by uuid", description = "Provide an uuid to look up a specific layer from database", tags = {
			"Layer" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieve one layer", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Layer.class))),
			@ApiResponse(responseCode = "404", description = "Layer not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@GetMapping("/withFieldsAndResource/maps/{mapSlug}/{mode}/{uuid}")
	public ResponseEntity<?> findSimpleLayerWithFieldsAndResource(@PathVariable String mapSlug,
			@PathVariable String mode, @PathVariable("uuid") UUID id) throws EntityNotFoundException {
		LayerSimpleWithFieldsAndResourcesDto withFieldsAndResourceDto = layerService.getLayerWithFieldsAndResources(id,
				mapSlug, mode, true);
		return new ResponseEntity<LayerSimpleWithFieldsAndResourcesDto>(withFieldsAndResourceDto, HttpStatus.OK);
	}

	@GetMapping("/withFieldsAndResource/edit/{uuid}")
	public ResponseEntity<?> findSimpleLayerWithFieldsAndResourceEditMode(@PathVariable("uuid") UUID id)
			throws EntityNotFoundException {
		LayerSimpleWithFieldsAndResourcesDto withFieldsAndResourceDto = layerService.getLayerWithFieldsAndResources(id,
				null, "edit", true);
		return new ResponseEntity<LayerSimpleWithFieldsAndResourcesDto>(withFieldsAndResourceDto, HttpStatus.OK);
	}

	@Operation(summary = "Find LayerSimpleWithFieldsAndResourceDto by uuid", description = "Provide an uuid to look up a specific layer from database", tags = {
			"Layer" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieve one layer", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Layer.class))),
			@ApiResponse(responseCode = "404", description = "Layer not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@GetMapping("/public/withFieldsAndResource/maps/{mapSlug}/{mode}/{uuid}")
	public ResponseEntity<?> findPublicSimpleLayerWithFieldsAndResource(@PathVariable String mapSlug,
			@PathVariable String mode, @PathVariable("uuid") UUID id) throws EntityNotFoundException {
		LayerSimpleWithFieldsAndResourcesDto withFieldsAndResourceDto = layerService.getLayerWithFieldsAndResources(id,
				mapSlug, mode, false);
		return new ResponseEntity<LayerSimpleWithFieldsAndResourcesDto>(withFieldsAndResourceDto, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('LAYER_READ_AUTHORITY')")
	@GetMapping("/sharedWithOthersInMap/{mapId}")
	public ResponseEntity<?> findAllLayersSharedInMap(@PathVariable UUID mapId,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir)
			throws EntityNotFoundException {
		PageDto<LayerSimpleWithFieldsDto> dtos = layerService.findAllLayersSharedInMap(mapId, page, limit, sort, dir);
		return new ResponseEntity<PageDto<LayerSimpleWithFieldsDto>>(dtos, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/{layerSlug}/shared-users")
	public ResponseEntity<?> getUsersSharingLayer(@PathVariable String layerSlug,
			@RequestParam(defaultValue = "") String search, @RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir) throws EntityNotFoundException {
		PageDto<UserCompleteDto> dtos = layerService.getUsersSharingLayerAutoComplete(layerSlug, search, page, limit,
				sort, dir);
		return new ResponseEntity<PageDto<UserCompleteDto>>(dtos, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('LAYER_READ_AUTHORITY')")
	@GetMapping("/sharedWithOthers/{mapId}")
	public ResponseEntity<?> findAllLayersSharedWithOthers(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir, @PathVariable UUID mapId) throws EntityNotFoundException {
		PageDto<LayerSimpleWithFieldsDto> dtos = layerService.findAllSharedLayers(mapId, page, limit, sort, dir);
		return new ResponseEntity<PageDto<LayerSimpleWithFieldsDto>>(dtos, HttpStatus.OK);
	}

	@Operation(summary = "Find all layers by criteria exist in map ", description = "Look up all layers from database", tags = {
			"Layer" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all layers", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Layer.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('LAYER_READ_AUTHORITY')")
	@PostMapping("/search/sharedWithOthers/{mapId}")
	public ResponseEntity<?> findAllLayersInMap(
			@Parameter(description = "Provide a payload how has the attributes of layer", required = true) @RequestBody CommonFilter filter,
			@PathVariable UUID mapId, @RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir) {

		PageDto<LayerSimpleDto> layerSimpleDtos = layerService.findAllLayersByFilterInMap(filter, mapId, page, limit,
				sort, dir);
		return new ResponseEntity<PageDto<LayerSimpleDto>>(layerSimpleDtos, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('LAYER_READ_AUTHORITY')")
	@GetMapping("/layerSharedWithOthers/{source}/{layerId}")
	public ResponseEntity<?> findLayerSharedWithOthers(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir, @PathVariable UUID layerId,
			@PathVariable("source") String source) throws EntityNotFoundException {
		LayerSimpleWithOthersDto withOthersDto = layerService.getLayerWithOthers(layerId, source, page, limit, sort,
				dir);
		return new ResponseEntity<LayerSimpleWithOthersDto>(withOthersDto, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('LAYER_READ_AUTHORITY')")
	@GetMapping("/findByTypeLimit/Admin")
	public ResponseEntity<?> findLayersByTypeLimit() {
		List<LayerSimpleDto> layerSimpleDtos = layerService.findByTypeLimit(TypeLimit.ADMIN);
		return new ResponseEntity<List<LayerSimpleDto>>(layerSimpleDtos, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('LAYER_CLONE_AUTHORITY')")
	@PostMapping("/{slug}/clone")
	public ResponseEntity<?> cloneLayer(@PathVariable("slug") String layerSlug, @RequestBody CloneLayerDto cloneLayerDto) {
		return new ResponseEntity<LayerDto>(layerService.cloneLayer(layerSlug, cloneLayerDto), HttpStatus.OK);

	}

	@GetMapping("/{uuid}/has-data")
	public ResponseEntity<?> layerHasData(@PathVariable("uuid") UUID layerId) {
		return new ResponseEntity<Boolean>(layerService.checkIfLayerHasData(layerId), HttpStatus.OK);
	}

}
