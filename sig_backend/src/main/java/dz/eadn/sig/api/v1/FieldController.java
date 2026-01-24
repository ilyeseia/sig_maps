package dz.eadn.sig.api.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

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
import dz.eadn.sig.dto.FieldDto;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.model.Field;
import dz.eadn.sig.service.FieldService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1.0/fields")
@Tag(name = "field", description = "the Field API")
public class FieldController extends CommonController<Field, FieldDto> {

	@Autowired
	private FieldService fieldService;

	public FieldController() {
		super(Field.class);
	}

	@Operation(summary = "Create a field", description = "add a single field to databse", tags = { "Field" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Field created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Field.class))),
			@ApiResponse(responseCode = "409", description = "Field already exist"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('FIELD_CREATE_AUTHORITY_AUTHORITY')")
	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody FieldDto dto, BindingResult results) {
		return super.create(dto, results);
	}

	@Operation(summary = "Find Field by uuid", description = "Provide an uuid to look up a specific field from database", tags = {
			"Field" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieve one field", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Field.class))),
			@ApiResponse(responseCode = "404", description = "Field not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('FIELD_READ_AUTHORITY')")
	@GetMapping("/{uuid}")
	public ResponseEntity<?> find(@PathVariable("uuid") UUID uuid) throws EntityNotFoundException {
		return super.find(uuid);
	}

	@Operation(summary = "Find all fields ", description = "Look up all fields from database", tags = { "Field" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all fields", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Field.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('FIELD_READ_AUTHORITY')")
	@GetMapping
	public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir) {
		return super.findAllByPage(page, limit, sort, dir);
	}

	@Operation(summary = "Update a Field", description = "Provide an uuid to look up a specific field from database to updated", tags = {
			"Field" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Update one field", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Field.class))),
			@ApiResponse(responseCode = "404", description = "Field not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('FIELD_UPDATE_AUTHORITY')")
	@PutMapping("/{uuid}")
	public ResponseEntity<?> update(@PathVariable("uuid") UUID uuid, @Valid @RequestBody FieldDto dto,
			BindingResult results) {
		return super.update(uuid, dto, results);
	}

	@Operation(summary = "Delete a Field", description = "Provide an uuid to look up a specific field from database to deleted", tags = {
			"Field" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Delete one field", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Field.class))),
			@ApiResponse(responseCode = "404", description = "Field not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('FIELD_DELETE_AUTHORITY')")
	@DeleteMapping("/{uuid}")
	public ResponseEntity<?> delete(@PathVariable("uuid") UUID uuid) {
		return super.delete(uuid);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('FIELD_UPDATE_AUTHORITY')")
	@PutMapping("/sort")
	public ResponseEntity<?> sortFields(@RequestBody List<FieldDto> fields) {
		List<FieldDto> result = new ArrayList<FieldDto>();
		for (FieldDto dto : fields) {
			log.info("Updating {} with uuid {}", domainClass.getSimpleName(), dto.getId());
			String error = "Unable to update " + domainClass.getSimpleName() + " with uuid " + dto.getId()
					+ " not found.";
			FieldDto fieldDto = null;
			try {
				dto.setId(dto.getId());
				fieldDto = fieldService.save(dto);
			} catch (Exception e) {
				log.error(error + ":" + e.getMessage());
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}

			result.add(fieldDto);
		}

		return new ResponseEntity<List<FieldDto>>(result, HttpStatus.ACCEPTED);
	}
}
