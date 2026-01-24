package dz.eadn.sig.api.v1;

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
import dz.eadn.sig.dto.GroupCompleteDto;
import dz.eadn.sig.dto.GroupDto;
import dz.eadn.sig.dto.GroupSimpleDto;
import dz.eadn.sig.dto.GroupSimpleWithOthersDto;
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.model.Group;
import dz.eadn.sig.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour && LAMOUR Ameur && Chouaib LOKBANI
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/v1.0/groups")
public class GroupController extends CommonController<Group, GroupDto> {

	@Autowired
	private GroupService groupService;

	public GroupController() {
		super(Group.class);
	}

	@Operation(summary = "Create a group", description = "add a single group to databse", tags = { "Group" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Group created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Group.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('GROUP_CREATE_AUTHORITY')")
	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody GroupDto dto, BindingResult results) {
		return super.create(dto, results);
	}

	@Operation(summary = "Find Group by uuid", description = "Provide an uuid to look up a specific group from database", tags = {
			"Group" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieve one group", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Group.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('GROUP_READ_AUTHORITY')")
	@GetMapping("/{uuid}")
	public ResponseEntity<?> find(@PathVariable("uuid") UUID uuid) throws EntityNotFoundException {
		return super.find(uuid);
	}

	@Operation(summary = "Find Group by uuid", description = "Provide an uuid to look up a specific group from database", tags = {
			"Group" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieve one group", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Group.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('GROUP_READ_AUTHORITY')")
	@GetMapping("/groupSharedWithOthers/{source}/{groupId}")
	public ResponseEntity<?> findGroupWithOthers(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir, @PathVariable("groupId") UUID groupId,
			@PathVariable("source") String source) throws EntityNotFoundException {

		GroupSimpleWithOthersDto withOthersDto = groupService.getGroupWithOthers(groupId, source, page, limit, sort,
				dir);
		return new ResponseEntity<GroupSimpleWithOthersDto>(withOthersDto, HttpStatus.OK);
	}

	@Operation(summary = "Find all groups", description = "Look up all groups from database", tags = { "Group" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all groups", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Group.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('GROUP_READ_AUTHORITY')")
	@GetMapping
	public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir) {
		PageDto<GroupCompleteDto> pageDto = groupService.getAllGroupsCompleteByPage(page, limit, sort, dir);
		return new ResponseEntity<PageDto<GroupCompleteDto>>(pageDto, HttpStatus.OK);
	}

	@Operation(summary = "Update a Group", description = "Provide an uuid to look up a specific group from database to updated", tags = {
			"Group" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Update one group", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Group.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('GROUP_UPDATE_AUTHORITY')")
	@PutMapping("/{uuid}")
	public ResponseEntity<?> update(@PathVariable("uuid") UUID uuid, @Valid @RequestBody GroupDto dto,
			BindingResult results) {
		return super.update(uuid, dto, results);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('GROUP_READ_AUTHORITY')")
	@GetMapping("/count")
	public ResponseEntity<?> count() {
		return super.count();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('GROUP_READ_AUTHORITY')")
	@PostMapping("/search")
	public ResponseEntity<?> findAllByCriteria(
			@Parameter(description = "Provide a payload how has the attributes of layer", required = true) @RequestBody CommonFilter filter,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "100") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {

		PageDto<GroupCompleteDto> GroupSimpleDto = groupService.findAllGroupsCompleteByFilter(filter, page, limit, sort,
				dir);
		return new ResponseEntity<PageDto<GroupCompleteDto>>(GroupSimpleDto, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('USER_READ_AUTHORITY')")
	@PostMapping("/simple/search")
	public ResponseEntity<?> findAllSimpleGroupByFilter(
			@Parameter(description = "Provide a payload how has the attributes of layer", required = true) @RequestBody CommonFilter filter,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "100") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {

		PageDto<GroupSimpleDto> GroupSimpleDto = groupService.findAllGroupsSimpleByFilter(filter, page, limit, sort,
				dir);
		return new ResponseEntity<PageDto<GroupSimpleDto>>(GroupSimpleDto, HttpStatus.OK);
	}

	@Operation(summary = "Delete a Group", description = "Provide an uuid to look up a specific Group from database to deleted", tags = {
			"Group" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Delete one group", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Group.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('GROUP_DELETE_AUTHORITY')")
	@DeleteMapping("/{uuid}")
	public ResponseEntity<?> delete(@PathVariable("uuid") UUID uuid) {
		return super.delete(uuid);
	}

}
