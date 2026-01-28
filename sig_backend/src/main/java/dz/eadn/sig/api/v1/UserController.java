
package dz.eadn.sig.api.v1;

import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dz.eadn.sig.controller.common.CommonController;
import dz.eadn.sig.dto.CommonFilter;
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.dto.ProfileDto;
import dz.eadn.sig.dto.ResetPasswordDto;
import dz.eadn.sig.dto.UserCompleteDto;
import dz.eadn.sig.dto.UserDto;
import dz.eadn.sig.dto.UserSimpleDto;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.mapper.UserGroupMapper;
import dz.eadn.sig.mapper.UserMapper;
import dz.eadn.sig.model.User;
import dz.eadn.sig.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * @author Achrouf Abdenour && Ameur LAMOUR && LOKBANI Chouaib
 *
 */
@RestController
@RequestMapping("/api/v1.0/users")
public class UserController extends CommonController<User, UserDto> {

	@Autowired
	private UserService userService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserGroupMapper ugMapper;

	@Autowired
	private PasswordEncoder encoder;

	public UserController() {
		super(User.class);
	}

	@Operation(summary = "Create a new User", description = "add a single user to databse, A user must define the group which it will belong to", tags = {
			"User" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "User created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('USER_CREATE_AUTHORITY')")
	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody UserDto dto, BindingResult results) {
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

	@PostMapping("/change-password")
	public ResponseEntity<UserDto> changePassword(@RequestBody ResetPasswordDto dto) {
		User user = userService.findById(dto.getUserId());
		try {
			if (encoder.matches(dto.getOldPassword(), user.getPassword())) {
				user.setPassword(encoder.encode(dto.getNewPassword()));
				userService.save(userMapper.entityToDto(user));
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.CONFLICT);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('USER_READ_AUTHORITY')")
	@GetMapping("/count")
	public ResponseEntity<?> count() {
		return super.count();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('USER_READ_AUTHORITY')")
	@GetMapping("/findByName/{name}")
	public ResponseEntity<UserDto> findByUserName(@PathVariable("name") String username) {
		User user = userService.findByUsername(username);
		UserDto userDto = ugMapper.entityToDto(user);
		return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
	}

	@GetMapping("/currentUser/{username}")
	public ResponseEntity<ProfileDto> getCurrentUser(@PathVariable String username,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {
		ProfileDto profileDto = userService.getCurrentUser(username, page, limit, sort, dir);
		return new ResponseEntity<ProfileDto>(profileDto, HttpStatus.OK);
	}

	@Operation(summary = "Find all users ", description = "Look up all users from database", tags = { "User" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all layers", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = User.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('USER_READ_AUTHORITY')")
	@GetMapping
	public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir) {
		PageDto<UserCompleteDto> pageDto = userService.getAllUsersCompleteByPage(page, limit, sort, dir);
		return new ResponseEntity<PageDto<UserCompleteDto>>(pageDto, HttpStatus.OK);
	}

	@Operation(summary = "Update a User", description = "Provide an uuid to look up a specific user from database to updated", tags = {
			"User" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Update one layer", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('USER_UPDATE_AUTHORITY')")
	@PutMapping("/{uuid}")
	public ResponseEntity<?> update(@PathVariable("uuid") UUID uuid, @Valid @RequestBody UserDto dto,
			BindingResult results) {
		return super.update(uuid, dto, results);
	}

	@PutMapping("/update-profile/{uuid}")
	public ResponseEntity<?> updateProfile(@PathVariable("uuid") UUID uuid, @Valid @RequestBody UserDto dto,
									BindingResult results) {
		return super.update(uuid, dto, results);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('USER_READ_AUTHORITY')")
	@PostMapping("/search")
	public ResponseEntity<?> findAllByFilter(
			@Parameter(description = "Provide a payload how has the attributes of layer", required = true) @RequestBody CommonFilter filter,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "100") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {
		PageDto<UserCompleteDto> pageDto = userService.findAllUsersCompleteByFilter(filter, page, limit, sort, dir);
		return new ResponseEntity<PageDto<UserCompleteDto>>(pageDto, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('USER_READ_AUTHORITY')")
	@PostMapping("/simple/search")
	public ResponseEntity<?> findAllSimpleUserByFilter(
			@Parameter(description = "Provide a payload how has the attributes of layer", required = true) @RequestBody CommonFilter filter,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "100") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {

		PageDto<UserSimpleDto> userSimpleDto = userService.findAllUsersSimpleByFilter(filter, page, limit, sort, dir);
		return new ResponseEntity<PageDto<UserSimpleDto>>(userSimpleDto, HttpStatus.OK);
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

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('USER_READ_AUTHORITY')")
	@PostMapping("/generatePassword")
	public ResponseEntity<?> generatePassword() {

		String password = userService.generatePassword();

		return new ResponseEntity<String>(password, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('USER_READ_AUTHORITY')")
	@PostMapping("/resetPassword/{userId}")
	public ResponseEntity<?> resetPassword(@PathVariable String userId, @RequestBody String newPassword) {

		userService.resetPassword(UUID.fromString(userId), newPassword);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@PostMapping("/auth/{authKey}")
	public ResponseEntity<?> getUserFromAuthKey(@PathVariable("authKey") String authKey) {
		return null;
	}
}
