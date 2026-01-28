package dz.eadn.sig.api.v1;

import java.util.List;
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
import dz.eadn.sig.dto.SettingsDto;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.model.Settings;
import dz.eadn.sig.service.SettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * @author Achrouf Abdenour && LAMOUR Ameur
 *
 */
@RestController
@RequestMapping("/api/v1.0/settings")
public class SettingsController extends CommonController<Settings, SettingsDto> {

	@Autowired
	private SettingsService settingsService;

	public SettingsController() {
		super(Settings.class);
	}

	@Operation(summary = "Create a setting", description = "add a single setting to databse", tags = { "Settings" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Setting created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Settings.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('SETTINGS_CREATE_AUTHORITY')")
	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody SettingsDto dto, BindingResult results) {
		return super.create(dto, results);
	}

	@Operation(summary = "Find Settings by uuid", description = "Provide an uuid to look up a specific setting from database", tags = {
			"Settings" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieve one setting", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Settings.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('SETTINGS_READ_AUTHORITY')")
	@GetMapping("/{uuid}")
	public ResponseEntity<?> find(@PathVariable("uuid") UUID uuid) throws EntityNotFoundException {
		return super.find(uuid);
	}

	@Operation(summary = "Find all settings", description = "Look up all settings from database", tags = { "Settings" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all groups", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Settings.class))) }),
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

	@GetMapping("/public/statistics")
	public ResponseEntity<?> getStatisticsSettings() {
		return new ResponseEntity<List<Settings>>(settingsService.getStatisticsSetting(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('SETTINGS_READ_AUTHORITY')")
	@PostMapping("/search")
	public ResponseEntity<?> findAllByCriteria(
			@Parameter(description = "Provide a payload how has the attributes of layer", required = true) @RequestBody CommonFilter filter,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "100") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {
		return super.findAllByFilter(filter, page, limit, sort, dir);
	}

	@Operation(summary = "Update a Settings", description = "Provide an uuid to look up a specific setting from database to updated", tags = {
			"Settings" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Update one setting", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Settings.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('SETTINGS_UPDATE_AUTHORITY')")
	@PutMapping("/{uuid}")
	public ResponseEntity<?> update(@PathVariable("uuid") UUID uuid, @Valid @RequestBody SettingsDto dto,
			BindingResult results) {
		return super.update(uuid, dto, results);
	}

	@Operation(summary = "Delete a setting", description = "Provide an uuid to look up a specific setting from database to deleted", tags = {
			"Settings" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Delete one setting", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Settings.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@Override

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('SETTINGS_DELETE_AUTHORITY')")
	@DeleteMapping("/{uuid}")
	public ResponseEntity<?> delete(@PathVariable("uuid") UUID uuid) {
		return super.delete(uuid);
	}

	@Operation(summary = "Find Settings by code", description = "Provide an code to look up a specific setting from database", tags = {
			"Settings" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieve one setting", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Settings.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('SETTINGS_READ_AUTHORITY')")
	@GetMapping("/findByType/{type}")
	public ResponseEntity<?> findByType(@PathVariable("type") String type) throws EntityNotFoundException {
		List<Settings> settings = settingsService.findByTypeAndEnabled(type, true);
		return new ResponseEntity<List<Settings>>(settings, HttpStatus.OK);
	}

	@GetMapping("/public/version")
	public ResponseEntity<?> getVersionNumber(@RequestParam() String code) throws EntityNotFoundException {
		Settings settings = settingsService.findByCode(code);
		String value = settings != null ? settings.getValue() : "";
		return new ResponseEntity<String>(value, HttpStatus.OK);
	}

	@Operation(summary = "Find Settings by code", description = "Provide an code to look up a specific setting from database", tags = {
			"Settings" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieve one setting by code", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Settings.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('SETTINGS_READ_AUTHORITY')")
	@GetMapping("/findByCode/{code}")
	public ResponseEntity<?> findByCode(@PathVariable("code") String code) throws EntityNotFoundException {
		Settings settings = settingsService.findByCode(code);
		String value = settings != null ? settings.getValue() : "";
		return new ResponseEntity<String>(value, HttpStatus.OK);
	}

	@GetMapping("/defaultBaseLayer")
	public ResponseEntity<?> findBaseLayer() throws EntityNotFoundException {
		Settings settings = settingsService.findByCode("SIG_DEFAULT_BASE_LAYER");
		String value = settings != null ? settings.getValue() : "";
		return new ResponseEntity<String>(value, HttpStatus.OK);
	}

}
