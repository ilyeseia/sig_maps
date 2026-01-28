package dz.eadn.sig.api.v1;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;

import dz.eadn.sig.dto.*;
import dz.eadn.sig.model.*;
import dz.eadn.sig.service.SettingsService;
import org.dom4j.DocumentException;
import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dz.eadn.sig.controller.common.CommonController;
import dz.eadn.sig.mapper.EntityElementSlugMapper;
import dz.eadn.sig.mapper.LayerUserMapper;
import dz.eadn.sig.service.EntityElementService;
import dz.eadn.sig.service.LayerService;
import dz.eadn.sig.service.NotificationService;
import dz.eadn.sig.util.EntityElementReader;
import dz.eadn.sig.util.EntityElementWriter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour && LAMOUR Ameur && LOKBANI Chouaib
 *
 */
@RestController
@RequestMapping("/api/v1.0/entityelements")
@Tag(name = "entityElement", description = "the entityElement API")
@Slf4j
public class EntityElementController extends CommonController<EntityElement, EntityElementDto> {

	@Autowired
	private LayerService layerService;

	@Autowired
	private LayerUserMapper luMapper;

	@Autowired
	private EntityElementService entityElementService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private EntityElementSlugMapper slugMapper;

	private static final String CODE_FOLDER_IMAGES = "SIG_DEFAULT_PATH_FOLDER_IMAGE";

	@Autowired
	private SettingsService settingsService;

	public EntityElementController() {
		super(EntityElement.class);
	}

	public void sendUserNotification(UserNotification userNotification) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// List<User> users = userNotification.getNotification().getLayer().getUsers();

		/*
		 * Set<String> usersSet = users.stream().map(user ->
		 * user.getUsername()).collect(Collectors.toSet()); for (SessionDto session :
		 * redisUtil.findAllSessions()) { if
		 * (!session.getUserName().equals(authentication.getName()) &&
		 * usersSet.contains(session.getUserName())) { String destination =
		 * "/notification/feature"; UserNotificationDto dto =
		 * userNotificationMapper.entityToDto(userNotification);
		 * messagingTemplate.convertAndSendToUser(session.getToken(), destination, dto);
		 * } }
		 */
	}

	@Operation(summary = "Create a entityElement", description = "add a single entityElement to databse", tags = {
			"entityElement" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "EntityElement created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityElement.class))),
			@ApiResponse(responseCode = "409", description = "EntityElement already exist"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('ENTITY_ELEMENT_CREATE_AUTHORITY')")
	@PostMapping("/{slug}")
	public ResponseEntity<?> create(@PathVariable("slug") String layerSlug,
			@Parameter(description = "EntityElement to add. Cannot null or empty.", required = true, schema = @Schema(implementation = EntityElement.class)) @RequestBody String featureJson,
			BindingResult results) {
		// Check if this layer belongs to this user
		layerService.CheckIfUserHasPrivilegeOnLayer(layerSlug, null, null, "write");
		EntityElementDto entityElementDto = new EntityElementDto();
		entityElementDto.setFeatureJson(featureJson);
		entityElementDto.setLayerSlug(layerSlug);
		ResponseEntity<?> response = super.create(entityElementDto, results);
//		EntityElement element = entityElementService.findById(((EntityElementDto) response.getBody()).getId());

//		Layer layer = layerService.findBySlug(layerSlug);
		// sendUserNotification(element, layer);
		return response;
	}

//	@Operation(summary = "Find entityElement by uuid", description = "Provide an uuid to look up a specific entityElement from database", tags = {
//			"entityElement" })
//	@ApiResponses(value = {
//			@ApiResponse(responseCode = "200", description = "Retrieve one entityElement", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityElement.class))),
//			@ApiResponse(responseCode = "404", description = "EntityElement not found"),
//			@ApiResponse(responseCode = "500", description = "Internal Server Error")
//
//	})
//
//	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('ENTITY_ELEMENT_READ_AUTHORITY')")
//	@GetMapping("/{uuid}")
//	public ResponseEntity<?> find(
//			@Parameter(description = "uuid of the entityElement to be obtained. Cannot be empty.", required = true) @PathVariable("uuid") UUID uuid)
//			throws EntityNotFoundException {
//		return super.find(uuid);
//	}

//	@Operation(summary = "Find all entityElements ", description = "Look up all entityElements from database", tags = {
//			"entityElement" })
//	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieve all entityElements", content = {
//			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = EntityElement.class))) }),
//			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
//	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") Integer page,
//			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
//			@RequestParam(defaultValue = "desc") String dir) {
//		return super.findAllByPage(page, limit, sort, dir);
//	}

	@Operation(summary = "Update a entityElement", description = "Provide an uuid to look up a specific entityElement from database to updated", tags = {
			"entityElement" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Update one entityElement", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityElement.class))),
			@ApiResponse(responseCode = "404", description = "EntityElement not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('ENTITY_ELEMENT_UPDATE_AUTHORITY')")
	@PutMapping("/{slug}/{id}")
	public ResponseEntity<?> update(@PathVariable("slug") String layerSlug,
			@Parameter(description = "uuid of the entityElement to be updated. Cannot be empty.", required = true) @PathVariable("id") UUID id,
			@Parameter(description = "EntityElement to add. Cannot null or empty.", required = true, schema = @Schema(implementation = EntityElement.class)) @RequestBody String featureJson,
			BindingResult results) {
		// Check if this layer belongs to this user
		layerService.CheckIfUserHasPrivilegeOnLayerAndEntityElement(layerSlug, null, null, "write", id);
		EntityElementDto entityElementDto = new EntityElementDto();
		entityElementDto.setFeatureJson(featureJson);
		entityElementDto.setLayerSlug(layerSlug);
		ResponseEntity<?> response = super.update(id, entityElementDto, results);
		EntityElement element = entityElementService.findById(((EntityElementDto) response.getBody()).getId());

		// sendUserNotification(element, layer);
		return response;
	}

	public void sendUserNotification(EntityElement element, Layer layer) {
		Optional<Notification> optionalNotification = notificationService.findByLayer(layer);

		if (optionalNotification.isPresent()) {
			Notification notification = optionalNotification.get();
			UserNotification userNotification = new UserNotification();
			// userNotification.setNotification(notification);
			userNotification.setViewed(false);
			// userNotification.setLink(element.getId());
			userNotification.setMessage(notificationService.evaluateMessage(notification, element));
			// userNotificationService.save(userNotificationMapper.entityToDto(userNotification));
			sendUserNotification(userNotification);
		}
	}

	@Operation(summary = "Delete a entityElement", description = "Provide an uuid to look up a specific entityElement from database to deleted", tags = {
			"entityElement" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Delete one entityElement", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityElement.class))),
			@ApiResponse(responseCode = "404", description = "EntityElement not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('ENTITY_ELEMENT_DELETE_AUTHORITY')")
	@DeleteMapping("/{uuid}/layers/{layerSlug}")
	public ResponseEntity<?> delete(@PathVariable String layerSlug,
			@Parameter(description = "uuid of the entityElement to be deleted. Cannot be empty.", required = true) @PathVariable("uuid") UUID uuid) {
		layerService.CheckIfUserHasPrivilegeOnLayer(layerSlug, null, null, "write");
		return super.delete(uuid);
	}

	@Operation(summary = "import entity elements from choosen file and format", description = "supported formats are geojson ,csv ,shape", tags = {
			"entityElement" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "importation succed"),
			@ApiResponse(responseCode = "404", description = "no entityElement to import was founded"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('ENTITY_ELEMENT_IMPORT_AUTHORITY')")
	@PostMapping("/import/{foldername}/{slug}/{ext}")
	public ResponseEntity<?> importFrom(@PathVariable("foldername") String foldername, @PathVariable("slug") String layerName, @PathVariable("ext") String dataName) {
		Layer layer = null;
		try {
			Settings settings = settingsService.findByCode(CODE_FOLDER_IMAGES);
			String path = null;
			if (settings != null) {
				path = Paths.get(settings.getValue()) + "/" +  foldername.replace(".", "/").replace("__", ".");
			}else{
				throw new RuntimeException("path not found");
			}
			File myObj = new File(path);
			InputStream targetStream = new FileInputStream(myObj);

			EntityElementReader reader = entityElementService.getReader(dataName);
			if (reader == null) {
				log.error("no reader found for file" + dataName);
				return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
			}

			LayerDto layerDto = new LayerDto();
			layerDto.setName(layerName);
			layer = reader.readEntityElements(layerDto, targetStream);

		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (layer == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			LayerDto result = luMapper.entityToDto(layer);
			return new ResponseEntity<LayerDto>(result, HttpStatus.OK);
		}
	}

	@Operation(summary = "export entity elements to choosen format", description = "supported formats are geojson ,csv ,shape", tags = {
			"entityElement" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "exportation succed"),
			@ApiResponse(responseCode = "404", description = "no entityElement to export"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })

	@PostMapping("/export/{slug}/{ext}")
	public void exportTo(@PathVariable("ext") String dataName, @PathVariable("slug") String layerSlug,
			@RequestParam(value = "file", required = false, defaultValue = "true") boolean exportToFile,
			@RequestBody String paylaod, HttpServletResponse response, @RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir) throws DocumentException {
		layerService.CheckIfUserHasPrivilegeOnLayer(layerSlug, null, "ENTITY_ELEMENT_MULTI_EXPORT_AUTHORITY", "write");
		try {

			List<EntityElement> entityElements = null;
			PageDto<EntityElementDto> pageDtos = null;
			PageDto<EntityElement> globalDtos = null;

			GlobalFilterDto globalFilterDto = new GlobalFilterDto();
			List<String> layersSlug = new ArrayList<>();

			ObjectMapper mapper = new ObjectMapper();

			CommonFilter filter = mapper.readValue(paylaod, CommonFilter.class);

			EntityElementWriter entityElementWriter = entityElementService.getWriter(dataName);

			Layer layer = layerService.findBySlug(layerSlug);

			if (layer != null) {
				List<Field> fields = null;

				layersSlug.add(layer.getSlug());

				List<String> fieldsSlug = layer.getFields().stream().filter(f -> !f.getType().equals(FieldType.IMAGE))
						.filter(f -> !f.getType().equals(FieldType.CAROUSEL))
						.sorted(Comparator.comparingInt(Field::getOrder)).map(f -> f.getSlug())
						.collect(Collectors.toList());

				globalFilterDto.setLayersSlug(layersSlug);
				globalFilterDto.setFieldsSlug(fieldsSlug);
				globalFilterDto.setLayerIds("('" + layer.getId() + "')");
				if(filter.getRules() != null && !filter.getRules().isEmpty())
					globalFilterDto.setSearchText(filter.getRules().get(0).getValue());

				if (filter.getCondition() == null) {
					List<Layer> layers = new ArrayList<Layer>();

					layers.add(layer);
					globalDtos = entityElementService.findAllBySearch(globalFilterDto, page, limit, sort, dir);

					entityElements = globalDtos.getContent();

				} else {
					pageDtos = entityElementService.findByAdvancedFilter(filter, layerSlug, page, limit, sort, dir);
					entityElements = slugMapper.dtosToEntitys(pageDtos.getContent());
				}
				response.setContentType(entityElementWriter.mimeType());

				if (exportToFile) {
					response.setHeader("Content-Disposition",
							"attachment; filename=" + layerSlug + entityElementWriter.extension());
				}
				response.setStatus(200);

				if (entityElements != null && !entityElements.isEmpty()) {

					for (EntityElement entityElement : entityElements) {

						Map<String, String> properties = entityElement.getProperties();
						Layer layerEE = entityElement.getLayer();

						if (layerEE != null) {

							fields = layerEE.getFields().stream().filter(field -> field.getVisible())
									.filter(f -> !f.getType().equals(FieldType.IMAGE))
									.filter(f -> !f.getType().equals(FieldType.CAROUSEL))
									.sorted(Comparator.comparingInt(Field::getOrder)).collect(Collectors.toList());

							Map<String, String> prop = new LinkedHashMap<>();

							fields.forEach(f -> {
									if(f.getType().equals(FieldType.SELECT)){
										prop.put(f.getSlug(),  properties.get(f.getSlug()) != null && properties.get(f.getSlug()).split(":").length > 1 ? properties.get(f.getSlug()).split(":")[1] : "");
									}else{
										prop.put(f.getSlug(), properties.get(f.getSlug())  != null? properties.get(f.getSlug()) : "");
									}
							});

							entityElement.setProperties(prop);


						}
					}
					layer.setFields(fields);

					entityElementWriter.writeEntityElements(layer, entityElements, response.getOutputStream());
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			response.setStatus(500);
		}
	}

	@PostMapping("/public/maps/{mapSlug}/search/{layerSlug}")
	public ResponseEntity<?> findAllByCriteria(@PathVariable String mapSlug,
			@Parameter(description = "Provide a payload how has the attributes of layer", required = true) @RequestBody CommonFilter params,
			@PathVariable String layerSlug, @RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir,
			@RequestParam(defaultValue = "false") boolean useFieldSlug) {
		log.info("Fetching {} layers");
		try {
			layerService.CheckIfUserHasPrivilegeOnLayer(layerSlug, mapSlug, "ENTITY_ELEMENT_READ_AUTHORITY", "read");
			PageDto<EntityElementDto> entityElementDtos = entityElementService.findByAdvancedFilter(params, layerSlug,
					page, limit, sort, dir);

			if (entityElementDtos == null) {
				String error = "no " + domainClass.getSimpleName() + " was found";
				log.error(error);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<PageDto<EntityElementDto>>(entityElementDtos, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('ENTITY_ELEMENT_READ_AUTHORITY') or hasAuthority('CONFIGURE_LAYER_STYLE_AUTHORITY')")
	@GetMapping("/property/{layerId}/{fieldName}")
	public ResponseEntity<?> groupByProperty(@PathVariable("layerId") UUID layerId,
			@PathVariable("fieldName") String fieldName) {
		List<String> result = entityElementService.groupByProperty(fieldName, layerId);
		return new ResponseEntity<List<String>>(result, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('ENTITY_ELEMENT_READ_AUTHORITY') or hasAuthority('CONFIGURE_LAYER_STYLE_AUTHORITY')")
	@GetMapping("/property/{layerId}/{fieldName}/classify")
	public ResponseEntity<?> findAllByPropertyAndClassification(@PathVariable("layerId") UUID layerId,
			@PathVariable("fieldName") String fieldName, @RequestParam CLASSIFICATIONMODE classification,
			@RequestParam int classes) {
		List<Map<String, Object>> result = entityElementService.findAllByProperty(fieldName, layerId, classification,
				classes);
		return new ResponseEntity<List<Map<String, Object>>>(result, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('ENTITY_ELEMENT_READ_AUTHORITY')")
	@GetMapping("/{fieldName}/{layerSlug}")
	public ResponseEntity<?> findPropertyByLayer(@PathVariable("fieldName") String fieldName,
			@PathVariable("layerSlug") String layerSlug) {

		List<Map<String, Object>> result = entityElementService.findPropertyByLayer(fieldName, layerSlug);

		return new ResponseEntity<List<Map<String, Object>>>(result, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('ENTITY_ELEMENT_READ_AUTHORITY')")
	@PostMapping("/convexHull")
	public ResponseEntity<?> getConvexHull(@RequestBody String multipoint) {

		String result = entityElementService.getConvexHull(multipoint);

		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('ENTITY_ELEMENT_READ_AUTHORITY')")
	@PostMapping("/centroid/{id}")
	public ResponseEntity<?> getCentroId(@PathVariable UUID id) {

		String result = entityElementService.getCentroId(id);

		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('ENTITY_ELEMENT_READ_AUTHORITY')")
	@PostMapping("/authorized")
	public ResponseEntity<?> isAuthorizedArea(@RequestBody String geometry) throws IOException {

		GeometryJSON json = new GeometryJSON();
		Geometry geom = json.read(geometry);

		boolean result = entityElementService.isAuthorizedArea(geom);

		return new ResponseEntity<Boolean>(result, HttpStatus.OK);
	}

	@PostMapping("/maps/{mapSlug}/globalSearch")
	public ResponseEntity<?> findAllBySearch(@PathVariable String mapSlug,
			@Parameter(description = "Provide a payload list of layers", required = true) @RequestBody GlobalFilterDto dto,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {
		log.info("Fetching {} layers");
		try {
			dto.getLayersSlug().forEach(slug -> {
				layerService.CheckIfUserHasPrivilegeOnLayer(slug, mapSlug, "ENTITY_ELEMENT_READ_AUTHORITY", "read");
			});
			PageDto<EntityElementDto> pageDto = new PageDto<>();

			PageDto<EntityElement> entityElements = entityElementService.findAllBySearch(dto, page, limit, sort, dir);

			if (entityElements != null) {
				pageDto.setContent(slugMapper.entitysToDtos(entityElements.getContent()));
				pageDto.setTotalElements(entityElements.getTotalElements());
			}

			else {
				String error = "no " + domainClass.getSimpleName() + " was found";
				log.error(error);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<PageDto<EntityElementDto>>(pageDto, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/layers/{layerSlug}")
	public ResponseEntity<?> getAlllEntityElementByLayer(@PathVariable String layerSlug) {
		log.info("Fetching {} layers");
		try {
			return new ResponseEntity<List<Map<String, Object>>>(
					entityElementService.getEntityElementForReporting(layerSlug), HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/layers")
	public ResponseEntity<?> getAlleleEntityElementByAdministrativelyLayer(@RequestParam("layerid") UUID layerId, @RequestParam("identifiant") String identifiant) {
		return new ResponseEntity<List<Map<String, Object>>>(
				entityElementService.getEntityElementForSecurityRestrictions(layerId, identifiant), HttpStatus.OK);
	}

	@PostMapping("/public/globalSearch")
	public ResponseEntity<?> findAllPublicBySearch(
			@Parameter(description = "Provide a payload list of layers", required = true) @RequestBody GlobalFilterDto dto,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {
		log.info("Fetching {} layers");
		try {
			dto.getLayersSlug().forEach(slug -> {
				layerService.CheckIfUserHasPrivilegeOnLayer(slug, null, null, "read");
			});
			PageDto<EntityElementDto> pageDto = new PageDto<>();

			PageDto<EntityElement> entityElements = entityElementService.findAllBySearch(dto, page, limit, sort, dir);

			if (entityElements != null) {
				pageDto.setContent(mapper.entitysToDtos(entityElements.getContent()));
				pageDto.setTotalElements(entityElements.getTotalElements());
			}

			else {
				String error = "no " + domainClass.getSimpleName() + " was found";
				log.error(error);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<PageDto<EntityElementDto>>(pageDto, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@PostMapping("/public/viewTable/globalSearch/{layerSlug}/maps/{mapSlug}")
	public ResponseEntity<?> searchInPublicViewTable(@PathVariable String layerSlug, @PathVariable String mapSlug, @RequestBody String searchText,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {

		PageDto<EntityElementSimpleDto> pageDto = null;
		ObjectMapper objectMapper = null;

		try {
			objectMapper = new ObjectMapper();
			Map<String, String> map = objectMapper.readValue(searchText, new TypeReference<Map<String, String>>() {
			});
			pageDto = entityElementService.findAllBySearchInViewTable(map.get("searchText"), layerSlug,mapSlug,page, limit,
					sort, dir);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<PageDto<EntityElementSimpleDto>>(pageDto, HttpStatus.OK);
	}

	@PostMapping("/maps/{mapSlug}/load/{layerSlug}")
	public ResponseEntity<?> findAllByLayer(@PathVariable String layerSlug, @PathVariable String mapSlug,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {
		log.info("Fetching {} layers");
		try {
			layerService.CheckIfUserHasPrivilegeOnLayer(layerSlug, mapSlug, "ENTITY_ELEMENT_READ_AUTHORITY", "read");

			PageDto<EntityElementSimpleDto> entityElementDtos = entityElementService.findAllByLayer(layerSlug, page,
					limit, sort, dir);

			if (entityElementDtos == null) {
				String error = "no " + domainClass.getSimpleName() + " was found";
				log.error(error);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<PageDto<EntityElementSimpleDto>>(entityElementDtos, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/public/load/{layerSlug}")
	public ResponseEntity<?> loadPublicLayer(@PathVariable String layerSlug,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {
		log.info("Fetching {} layers");
		try {
			layerService.CheckIfUserHasPrivilegeOnLayer(layerSlug, null, null, "read");
			PageDto<EntityElementSimpleDto> entityElementDtos = entityElementService.findAllByLayer(layerSlug, page,
					limit, sort, dir);

			if (entityElementDtos == null) {
				String error = "no " + domainClass.getSimpleName() + " was found";
				log.error(error);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<PageDto<EntityElementSimpleDto>>(entityElementDtos, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('ENTITY_ELEMENT_READ_AUTHORITY')")
	@PostMapping("/buffer")
	public ResponseEntity<?> getBuffer(@RequestBody String buffer) {

		ObjectMapper mapper = new ObjectMapper();

		BufferParamsDto paramsDto = null;
		try {
			paramsDto = mapper.readValue(buffer, BufferParamsDto.class);
		} catch (Exception e) {
			log.info(e.getMessage());

		}

		String result = entityElementService.getBuffer(paramsDto.getCenter(), paramsDto.getRadius());

		return new ResponseEntity<String>(result, HttpStatus.OK);
	}


	@GetMapping("/public/nearbiest")
	public ResponseEntity<?> getNearbiestEntityElementsForPublic(
			@RequestParam(required = false) UUID selectedEntityElement,
			@RequestParam() UUID targetLayer,
			@RequestParam() String targetLayerSlug,
			@RequestParam() String mapSlug,
			@RequestParam(required = false) double perimeter,
			@RequestParam(required = false) String unit,
			@RequestParam() String geometry,
			@RequestParam() PostgisOperation operation,
			@RequestParam() boolean intersection,
			@RequestParam(required = false) PostgisOperation algebraOperation,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir){
		Pageable pageable = PageRequest.of(page, limit);
		return new ResponseEntity<PageDto<EntityElementDto>>(entityElementService.findNearestEntityElements(selectedEntityElement, targetLayer,targetLayerSlug, mapSlug,perimeter, unit, geometry, operation,intersection, algebraOperation, pageable), HttpStatus.OK);
	}

}
