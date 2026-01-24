package dz.eadn.sig.api.v1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dz.eadn.sig.dto.CommonFilter;
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.dto.SessionDto;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.service.SessionService;
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
@Slf4j
@RestController
@RequestMapping("/api/v1.0/sessions")
public class SessionController {

	@Autowired
	private SessionService sessionService;

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('SESSION_READ_AUTHORITY')")
	@GetMapping
	public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir) {
		PageDto<SessionDto> result = sessionService.findAll(page, limit, sort, dir);
		return new ResponseEntity<>(result, HttpStatus.OK);
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
		List<SessionDto> result = sessionService.findByAdvancedFilter(filter, page, limit, sort, dir);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('USER_READ_AUTHORITY')")
	@GetMapping("/count")
	public ResponseEntity<?> count() {
		return new ResponseEntity<Long>(sessionService.count(), HttpStatus.OK);
	}

	@Operation(summary = "remove specific user session", description = "remove user session by giving user name and token ", tags = {
			"Session" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Sessions found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SessionDto.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('SESSION_DELETE_AUTHORITY')")
	@DeleteMapping("/{username}")
	public ResponseEntity<?> delete(@PathVariable("username") String userName) {
		try {
			sessionService.delete(userName);
		} catch (Exception e) {
			String error = "Unable to remove session";
			log.error(error + ":" + e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return ResponseEntity.ok().build();
	}
}

