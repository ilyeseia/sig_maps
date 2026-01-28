package dz.eadn.sig.api.v1;

import java.util.UUID;

import jakarta.validation.Valid;

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
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.dto.PermissionDto;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.model.Permission;
import dz.eadn.sig.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * @author && LAMOUR Ameur
 *
 */
@RestController
@Slf4j
@RequestMapping("/api/v1.0/permissions")
public class PermissionController extends CommonController<Permission, PermissionDto> {

	@Autowired
	private PermissionService permissionService;

	public PermissionController() {
		super(Permission.class);
	}

	@Operation(summary = "Create a new Permission", description = "add a single user to databse, A user must define the group which it will belong to", tags = {
			"Permission" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Permission created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Permission.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('PERMISSION_CREATE_AUTHORITY')")
	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody PermissionDto dto, BindingResult results) {
		return super.create(dto, results);
	}

	@Operation(summary = "Find Permission by uuid", description = "Provide an uuid to look up a specific user from database", tags = {
			"Permission" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieve one permission", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Permission.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('PERMISSION_READ_AUTHORITY')")
	@GetMapping("/{uuid}")
	public ResponseEntity<?> find(@PathVariable("uuid") UUID uuid) throws EntityNotFoundException {
		return super.find(uuid);
	}

	@Operation(summary = "Find all users ", description = "Look up all users from database", tags = { "Permission" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all layers", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Permission.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('PERMISSION_READ_AUTHORITY')")
	@GetMapping
	public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir) {

		return super.findAllByPage(page, limit, sort, dir);
	}

	@Operation(summary = "Update a Permission", description = "Provide an uuid to look up a specific user from database to updated", tags = {
			"Permission" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Update one permission", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Permission.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('PERMISSION_UPDATE_AUTHORITY')")
	@PutMapping("/{uuid}")
	public ResponseEntity<?> update(@PathVariable("uuid") UUID uuid, @Valid @RequestBody PermissionDto dto,
			BindingResult results) {
		return super.update(uuid, dto, results);
	}

	@Operation(summary = "Delete a Permission", description = "Provide an uuid to look up a specific Permission from database to deleted", tags = {
			"Permission" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Delete one Permission", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Permission.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('PERMISSION_DELETE_AUTHORITY')")
	@DeleteMapping("/{uuid}")
	public ResponseEntity<?> delete(@PathVariable("uuid") UUID uuid) {
		return super.delete(uuid);
	}

	@Operation(summary = "Create a new Permission", description = "add a single user to databse, A user must define the group which it will belong to", tags = {
			"Permission" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Permission created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Permission.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('PERMISSION_CREATE_AUTHORITY')")
	@PostMapping("/search")
	public ResponseEntity<?> findAllByFilter(
			@Parameter(description = "Provide a payload how has the attributes of permission", required = true) @RequestBody CommonFilter filter,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {
		try {	

			PageDto<PermissionDto> permissions = permissionService.findAllByFilter(filter, page, limit, sort, dir);

			return new ResponseEntity<PageDto<PermissionDto>>(permissions, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
