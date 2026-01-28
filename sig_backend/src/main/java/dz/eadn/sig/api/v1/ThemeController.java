package dz.eadn.sig.api.v1;


import dz.eadn.sig.controller.common.CommonController;
import dz.eadn.sig.dto.*;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.model.Theme;
import dz.eadn.sig.service.ThemeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * @author  Chouaib LOKBANI
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/v1.0/themes")
@Tag(name = "themes", description = "the map themes API")
public class ThemeController extends CommonController<Theme, ThemeDto> {
    public ThemeController() {
        super(Theme.class);
    }

    @Autowired
    private ThemeService themeService;



    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ThemeDto dto, BindingResult results) {
        return super.create(dto, results);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<?> update(@PathVariable("uuid") UUID uuid, @Valid @RequestBody ThemeDto dto,
                                    BindingResult results) {
        return super.update(uuid, dto, results);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('CONFIGURE_LAYER_STYLE_AUTHORITY')")
    @DeleteMapping("/{theme-id}/maps/{map-id}")
    public ResponseEntity<?> delete(@PathVariable("theme-id") UUID themeId, @PathVariable("map-id") UUID mapId) {
        return themeService.deleteTheme(themeId, mapId);
    }

    @GetMapping("/maps/{map-id}")
    public ResponseEntity<?> findAllMapThemes(@PathVariable("map-id") UUID mapId) {
        return new ResponseEntity<List<ThemeSimpleDto>>(themeService.findAllMapThemes(mapId), HttpStatus.OK);
    }

    @GetMapping("/{theme-id}/maps/{map-id}/default")
    public void setStyleAsDefault(@PathVariable("theme-id") UUID themeId, @PathVariable("map-id") UUID mapId) throws EntityNotFoundException {
        themeService.setDefaultMapTheme(themeId, mapId);
    }

}




























