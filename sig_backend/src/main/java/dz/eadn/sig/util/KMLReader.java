/**
 * 
 */
package dz.eadn.sig.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.kml.v22.KMLConfiguration;
import org.geotools.xsd.Parser;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import dz.eadn.sig.dto.LayerDto;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.service.EntityElementService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour
 *
 */

@Component
@Slf4j
public class KMLReader implements EntityElementReader {
	@Autowired
	private EntityElementService eeService;

	private SimpleFeatureCollection readFeatureCollection(InputStream is)
			throws SAXException, ParserConfigurationException, IOException {
		Parser parser = new Parser(new KMLConfiguration());
		SimpleFeature f = (SimpleFeature) parser.parse(is);
		Collection placemarks = (Collection) f.getAttribute("Feature");

		DefaultFeatureCollection fc = new DefaultFeatureCollection();
		fc.addAll(placemarks);
		return fc;
	}

	@Override
	public Layer readEntityElements(LayerDto layerDto, InputStream is) {
		// TODO Auto-generated method stub
		Layer layer = null;
		try {
			layer = eeService.importEntityElementsFromFeatureCollection(layerDto, readFeatureCollection(is));
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return layer;
	}

	@Override
	public String dataName() {
		// TODO Auto-generated method stub
		return "kml";
	}
}
