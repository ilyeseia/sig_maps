package dz.eadn.sig.api.v1;

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
import dz.eadn.sig.dto.CommonFilter;
import dz.eadn.sig.dto.CustomJoinFilter;
import dz.eadn.sig.dto.TagDto;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.model.Tag;
import dz.eadn.sig.service.TagService;
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
@RequestMapping("/api/v1.0/tags")
public class TagController extends CommonController<Tag, TagDto> {

	@Autowired
	private TagService tagService;

	public TagController() {
		super(Tag.class);
	}

	@Operation(summary = "Create a tag", description = "add a single tag to databse", tags = { "tag" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Tag created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tag.class))),
			@ApiResponse(responseCode = "409", description = "Tag already exist"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('TAG_CREATE_AUTHORITY')")
	@PostMapping
	public ResponseEntity<?> create(
			@Parameter(description = "Tag to add. Cannot null or empty.", required = true, schema = @Schema(implementation = Tag.class)) @Valid @RequestBody TagDto dto,
			BindingResult results) {
		return super.create(dto, results);
	}

	@Operation(summary = "Find tag by uuid", description = "Provide an uuid to look up a specific tag from database", tags = {
			"tag" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieve one tag", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tag.class))),
			@ApiResponse(responseCode = "404", description = "Tag not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('TAG_READ_AUTHORITY')")
	@GetMapping("/{uuid}")
	public ResponseEntity<?> find(
			@Parameter(description = "uuid of the tag to be obtained. Cannot be empty.", required = true) @PathVariable("uuid") UUID uuid)
			throws EntityNotFoundException {
		return super.find(uuid);
	}

	@Operation(summary = "Find all tags ", description = "Look up all tags from database", tags = { "tag" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all tags", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Tag.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('TAG_READ_AUTHORITY')")
	@GetMapping
	public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir) {
		return super.findAllByPage(page, limit, sort, dir);
	}

	@Operation(summary = "Find all tags exist in type ", description = "Look up all tags from database related with other entity", tags = {
			"tag" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all tags", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Tag.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('TAG_READ_AUTHORITY')")
	@GetMapping("/tag/search")
	public ResponseEntity<?> findAllByType(@RequestBody CustomJoinFilter customJoinFilter) {
		log.info("Fetching {} tags");
		List<Object> result = tagService.findAllByType(customJoinFilter);

		if (result == null) {
			String error = "no " + domainClass.getSimpleName() + " was found";
			log.error(error);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<Object>>(result, HttpStatus.OK);

	}

	@Operation(summary = "Find all tags exist in name ", description = "Look up all tags from database listed by name", tags = {
			"tag" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all tags", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Tag.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('TAG_READ_AUTHORITY')")
	@GetMapping("/search")
	public ResponseEntity<?> findAllByCriteria(@RequestBody CommonFilter filter,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit,
			@RequestParam(defaultValue = "id") String sort, String dir) {
		return super.findAllByCriteria(filter, page, limit, sort, dir);
	}

	@Operation(summary = "Update a tag", description = "Provide an uuid to look up a specific tag from database to updated", tags = {
			"tag" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Update one tag", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tag.class))),
			@ApiResponse(responseCode = "404", description = "Tag not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('TAG_UPDATE_AUTHORITY')")
	@PutMapping("/{uuid}")
	public ResponseEntity<?> update(
			@Parameter(description = "uuid of the tag to be updated. Cannot be empty.", required = true) @PathVariable("uuid") UUID uuid,
			@Parameter(description = "Tag to add. Cannot null or empty.", required = true, schema = @Schema(implementation = TagDto.class)) @Valid @RequestBody TagDto tagDto,
			BindingResult results) {
		return super.update(uuid, tagDto, results);
	}

	@Operation(summary = "Delete a tag", description = "Provide an uuid to look up a specific tag from database to deleted", tags = {
			"tag" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Delete one tag", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tag.class))),
			@ApiResponse(responseCode = "404", description = "Tag not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('TAG_DELETE_AUTHORITY')")
	@DeleteMapping("/{uuid}")
	public ResponseEntity<?> delete(
			@Parameter(description = "uuid of the tag to be deleted. Cannot be empty.", required = true) @PathVariable("uuid") UUID uuid) {
		return super.delete(uuid);
	}

}
