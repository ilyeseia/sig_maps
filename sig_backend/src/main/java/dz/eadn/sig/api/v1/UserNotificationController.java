/**
 * 
 */
package dz.eadn.sig.api.v1;

import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import dz.eadn.sig.dto.UserCompleteDto;
import dz.eadn.sig.dto.UserNotificationDto;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.model.Notification;
import dz.eadn.sig.model.User;
import dz.eadn.sig.model.UserNotification;
import dz.eadn.sig.repository.UserRepository;
import dz.eadn.sig.service.UserNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour && AMEUR LAMOUR
 *
 */

@RestController
@Slf4j
@RequestMapping("/api/v1.0/user_notifications")
public class UserNotificationController {

	@Autowired
	private UserNotificationService userNotificationService;

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/search/{viewed}")
	public ResponseEntity<?> findAllByFilter(
			@Parameter(description = "Provide a payload how has the attributes of layer", required = true) @RequestBody CommonFilter filter,
			@PathVariable Boolean viewed, @RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "100") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir) {
		PageDto<UserNotificationDto> pageDto = userNotificationService.findAllNotificationsByFilter(filter, viewed,
				page, limit, sort, dir);
		return new ResponseEntity<PageDto<UserNotificationDto>>(pageDto, HttpStatus.OK);
	}

	@Operation(summary = "Find all users ", description = "Look up all users from database", tags = { "Notification" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all layers", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Notification.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping("/viewed/{viewed}")
	public ResponseEntity<?> findAllByViewed(@PathVariable Boolean viewed,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = null;

		Optional<User> optional = userRepository.findByUsername(authentication.getName());
		if (optional.isPresent())
			user = optional.get();

		PageDto<UserNotificationDto> userNotificationsDto = userNotificationService.findAllNotifications(user, viewed,
				page, limit, sort, dir);

		return new ResponseEntity<PageDto<UserNotificationDto>>(userNotificationsDto, HttpStatus.OK);
	}

	@Operation(summary = "Update a Notification", description = "Provide an uuid to look up a specific user from database to updated", tags = {
			"Notification" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Update one Notification", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notification.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PutMapping("/viewed/{uuid}")
	public ResponseEntity<?> updateViewed(@PathVariable("uuid") UUID uuid) {

		UserNotificationDto userNotificationDto = userNotificationService.setViewed(uuid);

		return new ResponseEntity<UserNotificationDto>(userNotificationDto, HttpStatus.OK);

	}
}
