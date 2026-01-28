package dz.eadn.sig.api.v1;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.exceptions.GlobalException;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.multipart.MultipartFile;

import dz.eadn.sig.controller.common.CommonController;
import dz.eadn.sig.dto.ResourceValueDto;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.model.ResourceValue;
import dz.eadn.sig.service.ResourceValueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour, LOKBANI Chouaib
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/v1.0/resourcevalues")
@Tag(name = "resourceValue", description = "the ResourceValue API")
public class ResourceValueController extends CommonController<ResourceValue, ResourceValueDto> {

	@Autowired
	private ResourceValueService resourceValueService;

	public ResourceValueController() {
		super(ResourceValue.class);
	}

	@Operation(summary = "Create a resourceValue", description = "add a single resourceValue to database", tags = {
			"resourceValue" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "ResourceValue created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceValue.class))),
			@ApiResponse(responseCode = "409", description = "ResourceValue already exist"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override
	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody ResourceValueDto dto, BindingResult results) {
		if(resourceValueService.checkExistenceByResource(dto.getResourceId(), dto.getParentId(), dto.getValue())){
			throw new GlobalException("Cette valeur existe déjà !");
		}
		return super.create(dto, results);
	}

	@Operation(summary = "Find resourceValue by uuid", description = "Provide an uuid to look up a specific resourceValue from database", tags = {
			"resourceValue" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieve one resourceValue", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceValue.class))),
			@ApiResponse(responseCode = "409", description = "ResourceValue already exist"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override
	@GetMapping("/{uuid}")
	public ResponseEntity<?> find(@PathVariable("uuid") UUID uuid) throws EntityNotFoundException {

		return super.find(uuid);
	}

	@Operation(summary = "Find all resourcesValue ", description = "Look up all resourcesValue from database", tags = {
			"resourceValue" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all resourcesValue", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ResourceValue.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override
	@GetMapping
	public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") Integer page,
									 @RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
									 @RequestParam(defaultValue = "desc") String dir) {
		return super.findAllByPage(page, limit, sort, dir);
	}

	@Operation(summary = "Update a resourceValue", description = "Provide an uuid to look up a specific resourceValue from database to updated", tags = {
			"resourceValue" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Update one resourceValue", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceValue.class))),
			@ApiResponse(responseCode = "404", description = "ResourceValue not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PutMapping("/{uuid}")
	public ResponseEntity<?> update(@PathVariable("uuid") UUID uuid,@Valid @RequestBody ResourceValueDto dto,
									BindingResult results) {
		if(dto.getParentId() != null){
			if(resourceValueService.checkExistenceByParent(dto.getParentId(), dto.getValue(), uuid)){
				throw new GlobalException("Cette valeur existe déjà !");
			}
		}else {
			if(resourceValueService.checkExistenceByResource(dto.getResourceId(), dto.getValue(), uuid)){
				throw new GlobalException("Cette valeur existe déjà !");
			}
		}
		ResponseEntity<?> updatedDto =  super.update(uuid, dto, results);
		return updatedDto;
	}

	@Operation(summary = "Delete a resourceValue", description = "Provide an uuid to look up a specific resourceValue from database to deleted", tags = {
			"resourceValue" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Delete one resourceValue", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceValue.class))),
			@ApiResponse(responseCode = "404", description = "ResourceValue not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override
	@DeleteMapping("/{uuid}")
	public ResponseEntity<?> delete(@PathVariable("uuid") UUID uuid) {
		return super.delete(uuid);
	}

	@Operation(summary = "Find all resourcesValue ", description = "Look up all resourcesValue from database by resource id and parent id", tags = {
			"resourceValue" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all resourcesValue", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ResourceValue.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@GetMapping("/{resourceId}/{parentId}")
	public ResponseEntity<?> findAllByResourceIdAndParentId(@PathVariable UUID resourceId,
															@PathVariable UUID parentId) {
		List<ResourceValue> resourceValues = resourceValueService.findAllByResouceIdAndParentId(resourceId, parentId);

		return new ResponseEntity<List<ResourceValue>>(resourceValues, HttpStatus.OK);
	}

	@Operation(summary = "Import a resourceValue", description = "loading resourceValue to databse", tags = {
			"resourceValue" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "ResourceValue created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceValue.class))),
			@ApiResponse(responseCode = "409", description = "ResourceValue already exist"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping("/import")
	public ResponseEntity<?> importResourceValue(@RequestBody MultipartFile file) {
		List<List<String>> resourceValues = null;
		try {
			resourceValues = resourceValueService.readResourceValues(file.getInputStream());

		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<List<String>>>(resourceValues, HttpStatus.OK);
	}

	@GetMapping("/resource/{resourceId}")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('RESOURCE_READ_AUTHORITY')")
	public ResponseEntity<?> findResourceValues(
			@Parameter(description = "uuid of the resource to be obtained. Cannot be empty.", required = true) @PathVariable("resourceId") String resourceId, @RequestParam("search") String search, @RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir)
			throws EntityNotFoundException {

		return new ResponseEntity<PageDto<ResourceValueDto>>(resourceValueService.findByValueAndResource(resourceId, search, page, limit, sort, dir), HttpStatus.OK);
	}

	@GetMapping("/resource/{resourceId}/{rvParentId}")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('RESOURCE_READ_AUTHORITY')")
	public ResponseEntity<?> findResourceValuesByParent(
			@Parameter(description = "uuid of the resource to be obtained. Cannot be empty.", required = true) @PathVariable("resourceId") String resourceId,
			@Parameter(description = "uuid of the resource value parent. Cannot be empty.", required = true) @PathVariable("rvParentId") String rvParentId,
			@RequestParam("search") String search, @RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir)
			throws EntityNotFoundException {

		return new ResponseEntity<PageDto<ResourceValueDto>>(resourceValueService.findByValueAndResourceAndParent(resourceId, rvParentId, search, page, limit, sort, dir), HttpStatus.OK);
	}

	@PostMapping("/importfile")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('RESOURCE_READ_AUTHORITY')")
	public ResponseEntity<?> importResourceValues(@RequestBody List<ResourceValueDto> resourceValueDtoLIST, @RequestParam("with-missing-values") boolean withMissingValue)
			throws EntityNotFoundException {
		return new ResponseEntity<HashMap<String, List<ResourceValueDto>>>(resourceValueService.importResourceValues(resourceValueDtoLIST, withMissingValue), HttpStatus.OK);
	}

	@DeleteMapping("/resource/{uuid}")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('RESOURCE_DELETE_AUTHORITY')")
	public ResponseEntity<?> deleteAllResourceValuesByResource(@PathVariable("uuid") UUID uuid)
			throws EntityNotFoundException {
		return new ResponseEntity<List<UUID>>(resourceValueService.deleteAllResourceValuesByResource(uuid), HttpStatus.OK);

	}

	@DeleteMapping("/{uuid}/delete-all")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('RESOURCE_DELETE_AUTHORITY')")
	public ResponseEntity<?> deleteAllResourceValuesParent(@PathVariable("uuid") UUID uuid)
			throws EntityNotFoundException {

		return new ResponseEntity<List<UUID>>(resourceValueService.deleteAllByParentId(uuid), HttpStatus.OK);
	}
}
