
package dz.eadn.sig.api.v1;

import java.util.UUID;

import jakarta.validation.Valid;

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
import dz.eadn.sig.dto.UserLogDto;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.model.User;
import dz.eadn.sig.model.UserLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour && Ameur LAMOUR
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/v1.0/logs")
public class UserLogController extends CommonController<UserLog, UserLogDto> {

	public UserLogController() {
		super(UserLog.class);
	}

	@Operation(summary = "Create a new User", description = "add a single user to databse, A user must define the group which it will belong to", tags = {
			"User" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "User created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('USER_CREATE_AUTHORITY')")
	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody UserLogDto dto, BindingResult results) {
		return super.create(dto, results);
	}

	@Operation(summary = "Find User by uuid", description = "Provide an uuid to look up a specific user from database", tags = {
			"User" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieve one layer", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('USER_READ_AUTHORITY')")
	@GetMapping("/{uuid}")
	public ResponseEntity<?> find(@PathVariable("uuid") UUID uuid) throws EntityNotFoundException {
		return super.find(uuid);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('USER_READ_AUTHORITY')")
	@GetMapping("/count")
	public ResponseEntity<?> count() {
		return super.count();
	}

	@Operation(summary = "Find all users ", description = "Look up all users from database", tags = { "User" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all layers", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = User.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('USER_READ_AUTHORITY')")
	@GetMapping
	public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "100") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir) {
		return super.findAllByPage(page, limit, sort, dir);
	}

	@Operation(summary = "Update a User", description = "Provide an uuid to look up a specific user from database to updated", tags = {
			"User" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Update one layer", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('USER_UPDATE_AUTHORITY')")
	@PutMapping("/{uuid}")
	public ResponseEntity<?> update(@PathVariable("uuid") UUID uuid, @Valid @RequestBody UserLogDto dto,
			BindingResult results) {
		return super.update(uuid, dto, results);
	}

	@Operation(summary = "Delete a User", description = "Provide an uuid to look up a specific User from database to deleted", tags = {
			"User" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Delete one user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('USER_READ_AUTHORITY')")
	@DeleteMapping("/{uuid}")
	public ResponseEntity<?> delete(@PathVariable("uuid") UUID uuid) {
		return super.delete(uuid);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('PERMISSION_CREATE_AUTHORITY')")
	@PostMapping("/search")
	public ResponseEntity<?> findAllByFilter(
			@Parameter(description = "Provide a payload how has the attributes of permission", required = true) @RequestBody CommonFilter filter,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {
		return super.findAllByFilter(filter, page, limit, sort, dir);
	}
}
