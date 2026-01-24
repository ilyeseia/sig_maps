package dz.eadn.sig.util;

import dz.eadn.sig.dto.StyleDto;
import dz.eadn.sig.exceptions.GlobalException;
import dz.eadn.sig.model.SymbologyType;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.process.function.ProcessFunctionFactory;
import org.geotools.sld.SLDConfiguration;
import org.geotools.styling.*;
import org.geotools.styling.Stroke;
import org.geotools.util.factory.GeoTools;
import org.geotools.xml.styling.SLDParser;
import org.geotools.xml.styling.SLDTransformer;
import org.geotools.xsd.Configuration;
import org.geotools.xsd.Parser;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

/***
 * Author C.LOKBANI
 */

@Component
public class GeoToolsServiceImpl implements GeoToolsService {

	private StyleFactory sf = CommonFactoryFinder.getStyleFactory();
	private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
	private FilterFactory fff = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
	private String geometryAttributeName;

	StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
	static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();

	/***
	 * Style creation method for various topologies and symbologies
	 * 
	 * @param layerSlug
	 * @param symbolizer
	 * @param symbologyType
	 * @param styleDto
	 * @return String
	 */
	@Override
	public String createStyle(String styleName, String symbolizer, SymbologyType symbologyType, StyleDto styleDto) {
		StyledLayerDescriptor sld = styleFactory.createStyledLayerDescriptor();
		UserLayer layer = styleFactory.createUserLayer();
		sld.addStyledLayer(layer);
		Style style = styleFactory.createStyle();
		style.setName(styleName);
		style.getDescription().setAbstract("Default  layer Style");
		layer.setName(styleName);
		FeatureTypeStyle fts = sf.createFeatureTypeStyle();

		if (styleDto != null) {
			if(styleDto.getTransformation() != null){
				if(styleDto.getTransformation().get("name").equals("Heatmap")){
					symbolizer = "Raster";
				}else{
					symbolizer = "Point";
				}
				fts.setTransformation(createTransformation(styleDto.getTransformation()));
			}
			String finalLayerTopo = symbolizer;
			styleDto.getRules().stream().forEach(r -> {
				Rule rule = createRule(finalLayerTopo, r);
				fts.rules().add(rule);
			});
		}else{
			Rule rule = createRule(symbolizer, null);
			fts.rules().add(rule);
		}

		style.featureTypeStyles().add(fts);

		layer.addUserStyle(style);
		return generateSLD(sld);
	}

	/***
	 * Generate SLD that will be send to geoserver
	 * 
	 * @param sld
	 * @return String
	 */
	@Override
	public String generateSLD(StyledLayerDescriptor sld) {
		SLDTransformer styleTransform = new SLDTransformer();
		String xml = null;
		try {
			xml = styleTransform.transform(sld);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return xml;
	}

	/***
	 * parse the retrieved style from geoserver to StyledLayerDescriptor
	 * 
	 * @param style in sld with String type
	 * @return sld
	 */
	@Override
	public StyledLayerDescriptor parseSld(String style) {
		// create the parser with the sld configuration
		Configuration config = new SLDConfiguration();
		Parser parser = new Parser(config);

		// the xml instance document above
		InputStream xml = new ByteArrayInputStream(style.getBytes(StandardCharsets.UTF_8));
		// parse
		try {
			return (StyledLayerDescriptor) parser.parse(xml);
		} catch (IOException iox) {
			throw new GlobalException("Une erreur inattendue s'est produite");
		} catch (SAXException sax) {
			throw new GlobalException("Une erreur inattendue s'est produite");
		} catch (ParserConfigurationException px) {
			throw new GlobalException("Une erreur inattendue s'est produite");
		}
	}

	/***
	 * Create a Style object from a definition in a SLD document
	 * 
	 * @param sld Stirng
	 * @return Style object
	 */
	@Override
	public List<Rule> createStyleFromSLD(String sld) {
		try {
			SLDParser stylereader = new SLDParser(styleFactory, new ByteArrayInputStream(sld.getBytes(StandardCharsets.UTF_8)) );
			StyledLayerDescriptor parseSLD = stylereader.parseSLD();

				//most likely a style that is not a valid sld, try to actually parse out a
				// style and then wrap it in an sld
				SLDParser stylereader1 = new SLDParser( styleFactory, new ByteArrayInputStream(sld.getBytes(StandardCharsets.UTF_8)));
				Style styles[] = stylereader1.readXML();
				if (styles.length > 0) {
					NamedLayer l = styleFactory.createNamedLayer();
					l.addStyle(styles[0]);
					parseSLD.addStyledLayer(l);
					List<Rule> rules = l.styles().get(0).featureTypeStyles().get(0).rules();
					return rules;
				}


		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * Create rule for given  a giver symbolizer
	 * @param symbolizeName
	 * @param styleDto
	 * @return
	 */
	@Override
	public Rule createRule(String symbolizeName, HashMap<String, HashMap<String, String>> styleDto) {
		Symbolizer symbolizer = null;
		switch (symbolizeName) {
			case "Point":
				symbolizer = createPointStyle(styleDto);
				break;
			case "LineString":
				symbolizer = createLineStyle(styleDto);
				break;
			case "Raster":
				symbolizer = createRasterStyle(styleDto);
				break;
			case "Polygon":
			case "MultiPolygon":
				symbolizer = createPolygonStyle(styleDto);
				break;
			default:
				symbolizer = createPointStyle(styleDto);
		}

		Rule rule = sf.createRule();
		if (styleDto != null && styleDto.get("filter") != null) {
			rule.setName(styleDto.get("filter").get("field"));
			rule.setFilter(createFilter(styleDto.get("filter")));
		}

		rule.symbolizers().add(symbolizer);

		if (styleDto != null && styleDto.get("label") != null) {
			rule.symbolizers().add(createTextSymbolizer(styleDto.get("label"), symbolizeName));
		}

		return rule;
	}

	@Override
	public Symbolizer createPointStyle(HashMap<String, HashMap<String, String>> styleDto) {
		Graphic graphic = sf.createDefaultGraphic();
		graphic.graphicalSymbols().clear();

		if (styleDto != null && styleDto.get("icon") != null) {
			ExternalGraphic externalGraphic = sf.createExternalGraphic(styleDto.get("icon").get("url"), "image/png");
			graphic.graphicalSymbols().add(externalGraphic);
			graphic.setSize(styleDto != null ? ff.literal(styleDto.get("icon").get("size"))
					: ff.literal(DefaultStyle.POINT_SIZE));
			graphic.setRotation(styleDto != null ? ff.literal(styleDto.get("icon").get("rotation")) : ff.literal(0));
		} else {
			Stroke stroke = createStroke(styleDto);
			Fill fill = createFill(styleDto);
			Mark mark = createMark(fill, stroke,
					styleDto != null ? styleDto.get("mark").get("name") : DefaultStyle.DEFAULT_MARK);
			graphic.graphicalSymbols().add(mark);
			graphic.setSize(styleDto != null ? ff.literal(styleDto.get("mark").get("size"))
					: ff.literal(DefaultStyle.POINT_SIZE));
			graphic.setRotation(styleDto != null ? ff.literal(styleDto.get("mark").get("rotation")) : ff.literal(0));
		}

		return sf.createPointSymbolizer(graphic, geometryAttributeName);
	}

	@Override
	public TextSymbolizer createTextSymbolizer(HashMap<String, String> styleDto, String topo) {
		TextSymbolizer textSymbolizer = sf.createTextSymbolizer();
		Fill fill = sf.createFill(ff.literal(styleDto.get("fill") != null ? styleDto.get("fill") : DefaultStyle.FILL_COLOUR));
		Font font = sf.createFont(ff.literal(styleDto.get("police") != null ? styleDto.get("police") : DefaultStyle.FONT_FAMIlY),
				ff.literal(styleDto.get("fontStyle") != null ? styleDto.get("fontStyle") : DefaultStyle.FONT_STYLE),
				ff.literal(styleDto.get("fontWeight") != null ? styleDto.get("fontWeight") : DefaultStyle.FONT_WEIGHT),
				ff.literal(styleDto.get("policeSize") != null ? styleDto.get("policeSize") : DefaultStyle.FONT_SIZE));
		if(styleDto.get("property") != null){
			textSymbolizer.setLabel(ff.function("strSubstringStart", ff.property(styleDto.get("property")),
					ff.add(ff.function("strIndexOf", ff.property(styleDto.get("property")), ff.literal(":")), ff.literal(1))
			));
			textSymbolizer.setFill(fill);
			textSymbolizer.setFont(font);
		}

		if(topo.equals("LineString")){
			LinePlacement linePlacement = sf.createLinePlacement(ff.literal(styleDto.get("perpendicularOffset") != null ? styleDto.get("perpendicularOffset") : DefaultStyle.PERPENDICULAR_OFFSET));
			textSymbolizer.setLabelPlacement(linePlacement);
		}else{
			AnchorPoint anchorPoint =
					sf.createAnchorPoint(ff.literal(ff.literal(styleDto.get("anchorPointX") != null ? styleDto.get("anchorPointX") : DefaultStyle.ANCHOR_POINT_X)),
							ff.literal(ff.literal(styleDto.get("anchorPointY") != null ? styleDto.get("anchorPointY") : DefaultStyle.ANCHOR_POINT_Y))
					);

			PointPlacement pointPlacement =
					sf.createPointPlacement(anchorPoint, null, ff.literal(styleDto.get("rotation") != null ? styleDto.get("rotation") : DefaultStyle.LABEL_ROTATION));
			textSymbolizer.setLabelPlacement(pointPlacement);
		}
		return textSymbolizer;
	}

	@Override
	public Symbolizer createPolygonStyle(HashMap<String, HashMap<String, String>> styleDto) {
		Stroke stroke = createStroke(styleDto);
		Fill fill = createFill(styleDto);
		return sf.createPolygonSymbolizer(stroke, fill, geometryAttributeName);
	}

	@Override
	public Symbolizer createLineStyle(HashMap<String, HashMap<String, String>> styleDto) {
		Stroke stroke = createStroke(styleDto);
		return sf.createLineSymbolizer(stroke, geometryAttributeName);
	}

	@Override
	public Symbolizer createRasterStyle(HashMap<String, HashMap<String, String>> styleDto){
		RasterSymbolizer rasterSymbolizer = sf.createRasterSymbolizer();
		rasterSymbolizer.setGeometry(ff.property("the_geom"));
		rasterSymbolizer.setOpacity(ff.literal(.6));
		ColorMap colorMap = sf.createColorMap();
		colorMap.setType(ColorMap.TYPE_RAMP);
		ColorMapEntry colorMapEntry = sf.createColorMapEntry();
		colorMapEntry.setColor(ff.literal("#FFFFFF"));
		colorMapEntry.setQuantity(ff.literal(0));
		colorMapEntry.setOpacity(ff.literal(0));
		colorMapEntry.setLabel("no data");
		colorMap.addColorMapEntry(colorMapEntry);

		ColorMapEntry colorMapEntry1 = sf.createColorMapEntry();
		colorMapEntry1.setColor(ff.literal("#FFFFFF"));
		colorMapEntry1.setOpacity(ff.literal(0));
		colorMapEntry1.setQuantity(ff.literal(0.02));
		colorMapEntry1.setLabel("no data");
		colorMap.addColorMapEntry(colorMapEntry1);

		ColorMapEntry colorMapEntry2 = sf.createColorMapEntry();
		colorMapEntry2.setColor(ff.literal("#4444FF"));
		colorMapEntry2.setQuantity(ff.literal(0.1));
		colorMapEntry2.setLabel("values");
		colorMap.addColorMapEntry(colorMapEntry2);

		ColorMapEntry colorMapEntry3 = sf.createColorMapEntry();
		colorMapEntry3.setColor(ff.literal("#FF0000"));
		colorMapEntry3.setQuantity(ff.literal(0.5));
		colorMapEntry3.setLabel("values");
		colorMap.addColorMapEntry(colorMapEntry3);

		ColorMapEntry colorMapEntry4 = sf.createColorMapEntry();
		colorMapEntry4.setColor(ff.literal("#FFFF00"));
		colorMapEntry4.setQuantity(ff.literal(1));
		colorMapEntry4.setLabel("values");
		colorMap.addColorMapEntry(colorMapEntry4);
		rasterSymbolizer.setColorMap(colorMap);
		return rasterSymbolizer;
	}

	@Override
	public Stroke createStroke(HashMap<String, HashMap<String, String>> styleDto) {
		HashMap<String, String> stoke = null;
		if (styleDto != null) {
			stoke = styleDto.get("stroke");
		}
		return sf.createStroke(stoke != null ? ff.literal(stoke.get("color")) : ff.literal(DefaultStyle.LINE_COLOUR),
				stoke != null ? ff.literal(stoke.get("width")) : ff.literal(DefaultStyle.LINE_WIDTH),
				stoke != null ? ff.literal(stoke.get("opacity")) : ff.literal(DefaultStyle.OPACITY));
	}

	@Override
	public Fill createFill(HashMap<String, HashMap<String, String>> styleDto) {
		HashMap<String, String> fill = null;
		if (styleDto != null) {
			fill = styleDto.get("fill");
		}
		return sf.createFill(fill != null ? ff.literal(fill.get("color")) : ff.literal(DefaultStyle.FILL_COLOUR),
				fill != null ? ff.literal(fill.get("opacity") != null ? fill.get("opacity") : DefaultStyle.OPACITY) : ff.literal(DefaultStyle.OPACITY));
	}

	@Override
	public Mark createMark(Fill fill, Stroke stoke, String wellKnowName) {
		Mark mark = null;
		switch (wellKnowName) {
			case "circle":
				mark = sf.getCircleMark();
				break;
			case "star":
				mark = sf.getStarMark();
				break;
			case "triangle":
				mark = sf.getTriangleMark();
				break;
			default:
				mark = sf.getSquareMark();
		}
		mark.setFill(fill);
		mark.setStroke(stoke);
		return mark;
	}

	@Override
	public Filter createFilter(HashMap<String, String> styleDto) {
		switch (styleDto.get("operator")) {
		case "<":
			return ff.less(ff.property(styleDto.get("property")), ff.literal(styleDto.get("field")));
		case "<=":
			return ff.lessOrEqual(ff.property(styleDto.get("property")), ff.literal(styleDto.get("field")));
		case ">":
			return ff.greater(ff.property(styleDto.get("property")), ff.literal(styleDto.get("field")));
		case ">=":
			return ff.greaterOrEqual(ff.property(styleDto.get("property")), ff.literal(styleDto.get("field")));
		case "<>":
			return ff.between(ff.function("parseDouble", ff.property(styleDto.get("property"))), ff.literal(styleDto.get("field").split("-")[0]), ff.literal(styleDto.get("field").split("-")[1]));
		default:
			return ff.equals(ff.property(styleDto.get("property")), ff.literal(styleDto.get("field")));
		}

	}

	@Override
	public Expression createTransformation(HashMap<String, String> transformationParams) {
		ProcessFunctionFactory processFunctionFactory = new ProcessFunctionFactory();
		FunctionName functionName = null;
		for (FunctionName func : processFunctionFactory.getFunctionNames()) {
			if (transformationParams.get("name") != null && func.getName().equals(transformationParams.get("name"))) {
				functionName = func;
				break;
			}
		}

		if(functionName.equals(null)){
			throw new GlobalException("Une erreur inattendue s'est produite");
		}

		switch (transformationParams.get("name")){
			case "PointStacker":
				return ff.function(functionName.getFunctionName(),
						ff.function("parameter", ff.literal("data")),
						ff.function("parameter", ff.literal("cellSize"), ff.literal(transformationParams.get("cellSize") != null ? transformationParams.get("cellSize") : DefaultStyle.CELL_SIZE)),
						ff.function("parameter", ff.literal("outputBBOX"), ff.function("env", ff.literal("wms_bbox"))),
						ff.function("parameter", ff.literal("outputWidth"), ff.function("env", ff.literal("wms_width"))),
						ff.function("parameter", ff.literal("outputHeight"), ff.function("env", ff.literal("wms_height"))));
			case "Heatmap":
				return ff.function(functionName.getFunctionName(),
						ff.function("parameter", ff.literal("data")),
						ff.function("parameter", ff.literal("weightAttr"), ff.literal("pop2000")),
						ff.function("parameter", ff.literal("radiusPixels"), ff.function("env", ff.literal("radius"), ff.literal(100))),
						ff.function("parameter", ff.literal("pixelsPerCell"), ff.literal("10")),
						ff.function("parameter", ff.literal("outputBBOX"), ff.function("env", ff.literal("wms_bbox"))),
						ff.function("parameter", ff.literal("outputWidth"), ff.function("env", ff.literal("wms_width"))),
						ff.function("parameter", ff.literal("outputHeight"), ff.function("env", ff.literal("wms_height"))));
		}
		return null;

	}
}
