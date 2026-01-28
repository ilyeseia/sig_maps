package dz.eadn.sig.api.v1;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import dz.eadn.sig.model.ResourceValue;
import dz.eadn.sig.service.ResourceService;
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
import dz.eadn.sig.dto.CommonFilter;
import dz.eadn.sig.dto.ResourceDto;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.model.Resource;
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
 * @author Achrouf Abdenour && LAMOUR Ameur
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/v1.0/resources")
@Tag(name = "resource", description = "the Resource API")
public class ResourceController extends CommonController<Resource, ResourceDto> {

	@Autowired
	private ResourceService resourceService;

	@Autowired
	public ResourceController() {
		super(Resource.class);
	}

	@Operation(summary = "Create a resource", description = "add a single resource to databse", tags = { "resource" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Resource created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Resource.class))),
			@ApiResponse(responseCode = "409", description = "Resource already exist"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('RESOURCE_CREATE_AUTHORITY')")
	@PostMapping
	public ResponseEntity<?> create(
			@Parameter(description = "Resource to add. Cannot null or empty.", required = true, schema = @Schema(implementation = Resource.class)) @Valid @RequestBody ResourceDto dto,
			BindingResult results) {
		return super.create(dto, results);
	}

	@Operation(summary = "Create a collection of resources", description = "add a collection of resource to databse", tags = {
			"resource" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Resources created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Resource.class))),
			@ApiResponse(responseCode = "409", description = "Resource already exist"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('RESOURCE_CREATE_AUTHORITY')")
	@PostMapping("/collection")
	public ResponseEntity<?> createCollection(
			@Parameter(description = "Resource to add. Cannot null or empty.", required = true, schema = @Schema(implementation = Resource.class)) @RequestBody List<ResourceDto> dtos) {
		return super.createCollection(dtos);
	}

	@Operation(summary = "Find resource by uuid", description = "Provide an uuid to look up a specific resource from database", tags = {
			"resource" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieve one resource", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Resource.class))),
			@ApiResponse(responseCode = "404", description = "Resource not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('RESOURCE_READ_AUTHORITY')")
	@GetMapping("/{uuid}")
	public ResponseEntity<?> find(
			@Parameter(description = "uuid of the resource to be obtained. Cannot be empty.", required = true) @PathVariable("uuid") UUID uuid)
			throws EntityNotFoundException {

		return super.find(uuid);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('RESOURCE_READ_AUTHORITY')")
	@GetMapping("/{uuid}/children")
	public ResponseEntity<?> findResourceChildren(
			@Parameter(description = "Get all children for a given resource", required = true) @PathVariable("uuid") UUID uuid)
			throws EntityNotFoundException {
		return new ResponseEntity<List<ResourceDto>>(resourceService.getAllResourceChildren(uuid), HttpStatus.OK);
	}


	@Operation(summary = "Find all resources ", description = "Look up all resources from database", tags = {
			"resource" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all resources", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Resource.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('RESOURCE_READ_AUTHORITY')")
	@GetMapping
	public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir) {
		return super.findAllByPage(page, limit, sort, dir);
	}

	@Operation(summary = "Update a resource", description = "Provide an uuid to look up a specific resource from database to updated", tags = {
			"resource" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Update one resource", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Resource.class))),
			@ApiResponse(responseCode = "404", description = "Resource not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('RESOURCE_UPDATE_AUTHORITY')")
	@PutMapping("/{uuid}")
	public ResponseEntity<?> update(
			@Parameter(description = "uuid of the resource to be updated. Cannot be empty.", required = true) @PathVariable("uuid") UUID uuid,
			@Parameter(description = "Resource to add. Cannot null or empty.", required = true, schema = @Schema(implementation = Resource.class)) @Valid @RequestBody ResourceDto resourceDto,
			BindingResult results) {
		return super.update(uuid, resourceDto, results);
	}

	@Operation(summary = "Delete a resource", description = "Provide an uuid to look up a specific resource from database to deleted", tags = {
			"resource" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Delete one resource", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Resource.class))),
			@ApiResponse(responseCode = "404", description = "Resource not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('RESOURCE_DELETE_AUTHORITY')")
	@DeleteMapping("/{uuid}")
	public ResponseEntity<?> delete(
			@Parameter(description = "uuid of the resource to be deleted. Cannot be empty.", required = true) @PathVariable("uuid") UUID uuid) {
		return super.delete(uuid);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('RESOURCE_READ_AUTHORITY')")
	@GetMapping("/count")
	public ResponseEntity<?> count() {
		return super.count();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('RESOURCE_READ_AUTHORITY')")
	@PostMapping("/search")
	public ResponseEntity<?> findAllByCriteria(
			@Parameter(description = "Provide a payload how has the attributes of layer", required = true) @RequestBody CommonFilter filter,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "100") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {
		return super.findAllByFilter(filter, page, limit, sort, dir);
	}

}
