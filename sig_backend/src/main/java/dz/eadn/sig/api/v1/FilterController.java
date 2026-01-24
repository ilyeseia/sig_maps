package dz.eadn.sig.api.v1;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import dz.eadn.sig.dto.FilterDto;
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.dto.ShareFilterDto;
import dz.eadn.sig.dto.UserSimpleDtoWithFilterProjection;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.model.Filter;
import dz.eadn.sig.service.FilterService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LOKBANI Chouaib
 */
@Slf4j
@RestController
@RequestMapping("/api/v1.0/filters")
public class FilterController extends CommonController<Filter, FilterDto> {

	private FilterService filterService;

	public FilterController(FilterService filterService) {
		super(Filter.class);
		this.filterService = filterService;
	}

	@PostMapping("/layers/{layerSlug}")
	public ResponseEntity<?> newFilter(@RequestBody FilterDto filterDto, @PathVariable String layerSlug) {
		return new ResponseEntity<FilterDto>(filterService.addFilter(filterDto, layerSlug), HttpStatus.OK);
	}

	@PutMapping
	public ResponseEntity<?> updateFilter(@RequestBody FilterDto filterDto) {
		return new ResponseEntity<FilterDto>(filterService.updateFilter(filterDto), HttpStatus.OK);
	}

	@PostMapping("/share")
	public void shareFilter(@RequestBody ShareFilterDto shareFilterDto) {
		filterService.shareFilter(shareFilterDto);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/{filterId}/share-list")
	public ResponseEntity<?> getFilterShareList(@PathVariable UUID filterId,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer limit,
			@RequestParam(defaultValue = "createDate") String sort, @RequestParam(defaultValue = "desc") String dir)
			throws EntityNotFoundException {
		return new ResponseEntity<PageDto<UserSimpleDtoWithFilterProjection>>(
				filterService.getUsersBelongsToFilter(filterId, page, limit, sort, dir), HttpStatus.OK);
	}

	@DeleteMapping("/{uuid}")
	public ResponseEntity<?> deleteFilter(@PathVariable UUID uuid) {
		filterService.deleteFilter(uuid);
		return new ResponseEntity<UUID>(uuid, HttpStatus.OK);
	}

	@GetMapping("/by-user")
	public ResponseEntity<?> getFilterByUser(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
			@RequestParam(defaultValue = "desc") String dir) {
		return new ResponseEntity<PageDto<FilterDto>>(filterService.getUserFilters(page, limit, sort, dir),
				HttpStatus.OK);
	}

}
