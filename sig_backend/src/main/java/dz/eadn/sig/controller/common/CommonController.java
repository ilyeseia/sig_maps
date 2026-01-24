package dz.eadn.sig.controller.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import dz.eadn.sig.dto.CommonFilter;
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.exceptions.GlobalException;
import dz.eadn.sig.mapper.CommonMapper;
import dz.eadn.sig.service.common.CommonService;
import dz.eadn.sig.util.WITHUUID;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour && LAMOUR Ameur
 *
 * @param <CommonObject>
 * @param <CommonDto>
 */
@ComponentScan(basePackages = "dz.eadn.sig.security")
@Slf4j
public abstract class CommonController<CommonObject extends WITHUUID, CommonDto extends WITHUUID> {

	@Autowired
	private CommonService<CommonObject, CommonDto> commonService;

	protected Class<CommonObject> domainClass;

	@Autowired
	protected CommonMapper<CommonObject, CommonDto> mapper;

	public CommonController(Class<CommonObject> domainClass) {
		this.domainClass = domainClass;
	}

	public ResponseEntity<?> create(CommonDto dto, BindingResult results) {
		CommonDto commonDto = null;
		String error = "";
		try {
			if (results.hasErrors()) {
				for (FieldError fieldError : results.getFieldErrors()) {
					throw new GlobalException(fieldError.getDefaultMessage());
				}
			}

			commonDto = commonService.save(dto);

		} catch (Exception e) {
			error = "Unable to create " + domainClass.getSimpleName() + " : " + e.getMessage();
			log.error(error);
			return new ResponseEntity<String>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<CommonDto>(commonDto, HttpStatus.CREATED);
	}

	public ResponseEntity<?> createCollection(List<CommonDto> dtos) {
		List<CommonDto> results = new ArrayList<CommonDto>();

		for (CommonDto dto : dtos) {
			try {
				CommonDto commonDto = commonService.save(dto);
				results.add(commonDto);
			} catch (Exception e) {
				String error = "Unable to create " + domainClass.getSimpleName();
				log.error(error + ":" + e.getMessage());
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}

		}
		return new ResponseEntity<List<CommonDto>>(results, HttpStatus.CREATED);
	}

	public ResponseEntity<?> find(UUID uuid) throws EntityNotFoundException {

		log.info("Finding {} with uuid : {}", domainClass.getSimpleName(), uuid);
		CommonObject result = commonService.findById(uuid);

		if (result == null) {
			String error = domainClass.getSimpleName() + " with uuid " + uuid + " not found";
			log.error(error);
			throw new EntityNotFoundException(error);
		}

		CommonDto dto = mapper.entityToDto(result);
		return new ResponseEntity<CommonDto>(dto, HttpStatus.OK);
	}

	public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir) {
		log.info("Fetching {}", domainClass.getSimpleName());
		List<CommonObject> result = commonService.findAll(page, limit, sort, dir);

		if (result == null) {
			String error = "no " + domainClass.getSimpleName() + " was found";
			log.error(error);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		List<CommonDto> dtos = mapper.entitysToDtos(result);
		return new ResponseEntity<List<CommonDto>>(dtos, HttpStatus.OK);
	}

	public ResponseEntity<?> findAllByCriteria(
			@Parameter(description = "Provide a payload how has the attributes of layer", required = true) @RequestBody CommonFilter filter,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {
		log.info("Fetching {} layers");
		try {
			List<CommonObject> result = commonService.findByAdvancedFilter(filter, page, limit, sort, dir);

			if (result == null) {
				String error = "no " + domainClass.getSimpleName() + " was found";
				log.error(error);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			List<CommonDto> dtos = mapper.entitysToDtos(result);
			return new ResponseEntity<List<CommonDto>>(dtos, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> count() {
		return new ResponseEntity<Long>(commonService.count(), HttpStatus.OK);
	}

	public ResponseEntity<?> update(UUID id, CommonDto dto, BindingResult results) {
		String error = "";
		CommonDto commonDto = null;

		if (results.hasErrors()) {
			for (FieldError fieldError : results.getFieldErrors()) {
				if (!fieldError.getField().equals("password"))
					throw new GlobalException(fieldError.getDefaultMessage());
			}
		}

		try {
			dto.setId(id);
			commonDto = commonService.save(dto);
		} catch (Exception e) {
			error = e.getMessage();
			log.error(error);
			return new ResponseEntity<String>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<CommonDto>(commonDto, HttpStatus.ACCEPTED);
	}

	public ResponseEntity<?> delete(UUID id) {

		commonService.delete(id);

		return new ResponseEntity<UUID>(id, HttpStatus.OK);
	}

	public ResponseEntity<?> findAllByFilter(
			@Parameter(description = "Provide a payload how has the attributes of permission", required = true) @RequestBody CommonFilter filter,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir) {
		try {

			PageDto<CommonDto> pageDto = commonService.findAllByFilter(filter, page, limit, sort, dir);
			return new ResponseEntity<>(pageDto, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> findAllByPage(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir) {
		try {

			PageDto<CommonDto> pageDtos = commonService.findAllByPage(page, limit, sort, dir);

			return new ResponseEntity<>(pageDtos, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
