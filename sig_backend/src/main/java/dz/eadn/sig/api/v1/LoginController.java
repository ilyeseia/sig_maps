package dz.eadn.sig.api.v1;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dz.eadn.sig.dto.LoginRefreshRequest;
import dz.eadn.sig.exceptions.GlobalException;
import dz.eadn.sig.mapper.UserLogMapper;
import dz.eadn.sig.model.User;
import dz.eadn.sig.model.UserLog;
import dz.eadn.sig.security.JwtUtils;
import dz.eadn.sig.security.LoginRequest;
import dz.eadn.sig.security.LoginResponse;
import dz.eadn.sig.service.UserLogService;
import dz.eadn.sig.service.impl.UserDetailsImpl;
import dz.eadn.sig.service.impl.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * @author Ameur LAMOUR
 *
 */
@RestController
@RequestMapping("/api/v1.0")
public class LoginController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserLogService userLogService;

	@Autowired
	private UserLogMapper userLogMapper;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	private void updateUserLog(HttpServletRequest request, String token) {
		UserLog userLog = userLogService.findByToken(token);

		userLog.setLogoutDate(new Date());
		userLogService.save(userLogMapper.entityToDto(userLog));
	}

	private void createUserLog(HttpServletRequest request, String username, String token) {
		UserLog userLog = new UserLog();
		userLog.setLoginDate(new Date());
		userLog.setBrowserName(userLogService.getClientBrowser(request));
		userLog.setBrowserVersion(userLogService.getClientBrowserVersion(request));
		userLog.setUserIp(userLogService.getClientIpAddr(request));
		userLog.setUsername(username);
		userLog.setClientOS(userLogService.getClientOS(request));
		userLog.setToken(token);

		userLogService.save(userLogMapper.entityToDto(userLog));
	}

	@Operation(summary = "login page", description = "login into the application", tags = { "LoginResponse" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Login successed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshAuthentification(@RequestBody LoginRefreshRequest refreshRequest,
			HttpServletRequest request) {
		if (jwtUtils.validateJwtRefreshToken(refreshRequest.getRefreshToken())) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(refreshRequest.getUsername());
			if (userDetails != null) {
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
				String jwt = jwtUtils.generateJwtToken(authentication);
				String jwtRefreshToken = jwtUtils.generateRefreshToken(authentication);
				createUserLog(request, refreshRequest.getUsername(), jwt);
				return ResponseEntity.ok(new LoginResponse(jwt, jwtRefreshToken));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
			}
		} else
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
	}

	@Operation(summary = "login page", description = "login into the application", tags = { "LoginResponse" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Login successed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		User currentUser = userPrincipal.getUser();

		if (currentUser.getActivationDate() != null && currentUser.getActivationDate().after(new Date())) {
			throw new GlobalException("La date d'activation n'est pas commenc� ");
		}
		if (currentUser.getDesactivationDate() != null && currentUser.getDesactivationDate().before(new Date())) {
			throw new GlobalException("La date d'activation est d�pass�");
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		String jwtRefreshToken = jwtUtils.generateRefreshToken(authentication);
		createUserLog(request, loginRequest.getUsername(), jwt);
		return ResponseEntity.ok(new LoginResponse(jwt, jwtRefreshToken));
	}

	@Operation(summary = "logout page", description = "logout from the application")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Logout successed"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request, @RequestBody String refreshToken) {
		updateUserLog(request, refreshToken);
		jwtUtils.invalidateRelatedToken(refreshToken);
		return ResponseEntity.ok().build();
	}
}
