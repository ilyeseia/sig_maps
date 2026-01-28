package dz.eadn.sig.api.v1;


import dz.eadn.sig.controller.common.CommonController;
import dz.eadn.sig.dto.*;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.model.Style;
import dz.eadn.sig.service.LayerStylesService;
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
@RequestMapping("/api/v1.0/layer-styles")
@Tag(name = "layer-styles", description = "the Layer's styles API")
public class LayerStylesController extends CommonController<Style, LayerStyleDto> {

    @Autowired
    LayerStylesService layerStylesService;


    public LayerStylesController() {
        super(Style.class);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody LayerStyleDto dto, BindingResult results) {
        return super.create(dto, results);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<?> update(@PathVariable("uuid") UUID uuid, @Valid @RequestBody LayerStyleDto dto,
                                    BindingResult results) {
        return super.update(uuid, dto, results);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('CONFIGURE_LAYER_STYLE_AUTHORITY')")
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> delete(@PathVariable("uuid") UUID uuid) {
        return super.delete(uuid);
    }


    @PutMapping("/update-style")
    public ResponseEntity<?> updateStyle( @Valid @RequestBody LayerStyleDto layerStyleDto,
                                         BindingResult results) {
        return layerStylesService.updateLayerStyle(layerStyleDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('CONFIGURE_LAYER_STYLE_AUTHORITY')")
    @GetMapping("/{layer-slug}/maps/{map-layer-id}")
    public ResponseEntity<?> findSimpleLayerWithFields(@PathVariable("layer-slug") String layerSlug, @PathVariable("map-layer-id") UUID mapLayerId) throws EntityNotFoundException {
        return new ResponseEntity<List<LayerStyleSimpleDto>>(layerStylesService.getLayerStylesInTheme(layerSlug, mapLayerId), HttpStatus.OK);
    }

    @GetMapping("/{map-layer-id}/styles/{style-id}/default")
    public void setStyleAsDefault(@PathVariable("map-layer-id") UUID mapLayerId, @PathVariable("style-id") UUID styleId) throws EntityNotFoundException {
        layerStylesService.setDefaultLayerStyle(mapLayerId, styleId);
    }

}































