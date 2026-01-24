package dz.eadn.sig.service;

import dz.eadn.sig.dto.CommonFilter;
import dz.eadn.sig.dto.LayerDto;
import dz.eadn.sig.dto.GeoProcessingDto;
import dz.eadn.sig.dto.LayerSimpleWithFieldsDto;
import dz.eadn.sig.model.Layer;
import org.locationtech.jts.geom.Geometry;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;

/**
 * @author  LOKBANI Chouaib
 *
 */
public interface GeoProcessingService {

    List<LayerSimpleWithFieldsDto> spatialOperation(GeoProcessingDto spatialOpConfig, HttpServletResponse response);

    double getDistance(String unit, double perimeter);

    String generateCQLFilter(CommonFilter filterCriterias, Layer layer);

    Geometry combineIntoOneGeometry(List<Geometry> geometryCollection);

    void insertStatement(LayerDto savedLayer, String insertQuery, String username, String geom, String properties, boolean withCots);

    void insertStatement(LayerDto savedLayer, String username, String geom, String properties, boolean withCots);

    String intersectionQuery(String geom1, String geom2);

    String differenceQuery(String geom1, String geom2);


}
