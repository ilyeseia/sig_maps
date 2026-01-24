package dz.eadn.sig.api.v1;

import java.util.UUID;

import javax.validation.Valid;

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
import dz.eadn.sig.dto.SettingsTypeDto;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.model.SettingsType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * @author && LAMOUR Ameur
 *
 */
@RestController
@RequestMapping("/api/v1.0/settingsType")
public class SettingsTypeController extends CommonController<SettingsType, SettingsTypeDto> {

	public SettingsTypeController() {
		super(SettingsType.class);
	}

	@Operation(summary = "Create a SettingsType", description = "add a single SettingsType to databse", tags = { "SettingsType" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "SettingsType created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettingsType.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('SETTINGS_CREATE_AUTHORITY')")
	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody SettingsTypeDto dto, BindingResult results) {
		return super.create(dto, results);
	}

	@Operation(summary = "Find SettingsType by uuid", description = "Provide an uuid to look up a specific SettingsType from database", tags = {
			"SettingsType" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieve one SettingsType", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettingsType.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('SETTINGS_READ_AUTHORITY')")
	@GetMapping("/{uuid}")
	public ResponseEntity<?> find(@PathVariable("uuid") UUID uuid) throws EntityNotFoundException {
		return super.find(uuid);
	}

	@Operation(summary = "Find all settingsType", description = "Look up all settingsType from database", tags = { "SettingsType" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all groups", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SettingsType.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('SETTINGS_READ_AUTHORITY')")
	@GetMapping
	public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir) {
		return super.findAllByPage(page, limit, sort, dir);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('SETTINGS_READ_AUTHORITY')")
	@GetMapping("/count")
	public ResponseEntity<?> count() {
		return super.count();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('SETTINGS_READ_AUTHORITY')")
	@PostMapping("/search")
	public ResponseEntity<?> findAllByCriteria(
			@Parameter(description = "Provide a payload how has the attributes of layer", required = true) @RequestBody CommonFilter filter,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "100") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {
		return super.findAllByFilter(filter, page, limit, sort, dir);
	}

	@Operation(summary = "Update a SettingsType", description = "Provide an uuid to look up a specific SettingsType from database to updated", tags = {
			"SettingsType" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Update one SettingsType", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettingsType.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('SETTINGS_UPDATE_AUTHORITY')")
	@PutMapping("/{uuid}")
	public ResponseEntity<?> update(@PathVariable("uuid") UUID uuid, @Valid @RequestBody SettingsTypeDto dto,
			BindingResult results) {
		return super.update(uuid, dto, results);
	}

	@Operation(summary = "Delete a SettingsType", description = "Provide an uuid to look up a specific SettingsType from database to deleted", tags = {
			"SettingsType" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Delete one SettingsType", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettingsType.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('SETTINGS_DELETE_AUTHORITY')")
	@DeleteMapping("/{uuid}")
	public ResponseEntity<?> delete(@PathVariable("uuid") UUID uuid) {
		return super.delete(uuid);
	}

}
