package dz.eadn.sig.api.v1;

import dz.eadn.sig.dto.LayerDto;
import dz.eadn.sig.dto.GeoProcessingDto;
import dz.eadn.sig.dto.LayerSimpleWithFieldsDto;
import dz.eadn.sig.service.GeoProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author LOKBANI Chouaib
 *
 */
@RestController
@RequestMapping("/api/v1.0/geo-processing")
public class GeoProcessingController {

    @Autowired
    GeoProcessingService geoProcessingService;

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('GEOPROCESSING')")
    @PostMapping("/run")
    public ResponseEntity<?> performSpatialOperations(@RequestBody GeoProcessingDto spatialOpConfig, HttpServletResponse response) {
        return new ResponseEntity<List<LayerSimpleWithFieldsDto>>(geoProcessingService.spatialOperation(spatialOpConfig, response), HttpStatus.OK);
    }

}
