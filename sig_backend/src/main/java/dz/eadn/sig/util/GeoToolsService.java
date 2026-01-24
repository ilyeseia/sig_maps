package dz.eadn.sig.util;


import dz.eadn.sig.dto.StyleDto;
import dz.eadn.sig.model.SymbologyType;
import org.geotools.styling.*;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;

import java.util.HashMap;
import java.util.List;


/***
 * Author C.LOKBANI
 */


public interface GeoToolsService {

    String  createStyle(String styleName, String symbolizer, SymbologyType symbologyType, StyleDto styleDto);

    String generateSLD(StyledLayerDescriptor sld);

    StyledLayerDescriptor parseSld(String style);

    List<Rule> createStyleFromSLD(String sld);

    Rule createRule(String symbolizeName, HashMap<String, HashMap<String, String>> styleDto);

    Symbolizer createPolygonStyle(HashMap<String, HashMap<String, String>> styleDta);

    Symbolizer createLineStyle(HashMap<String, HashMap<String, String>> styleDta);

    Symbolizer createPointStyle(HashMap<String, HashMap<String, String>> styleDta);

    Symbolizer createRasterStyle(HashMap<String, HashMap<String, String>> styleDta);

    TextSymbolizer createTextSymbolizer(HashMap<String, String> styleDta, String topo);

    Stroke createStroke(HashMap<String, HashMap<String, String>> styleDto);

    Fill createFill(HashMap<String, HashMap<String, String>> styleDto);

    Mark createMark(Fill Fill, Stroke stoke , String wellKnowName);

    Filter createFilter(HashMap<String, String> styleDto);

    Expression createTransformation(HashMap<String, String> transformationParams);

}
