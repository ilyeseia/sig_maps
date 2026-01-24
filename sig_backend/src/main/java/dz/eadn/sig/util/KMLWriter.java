/**
 * 
 */
package dz.eadn.sig.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.kml.v22.KML;
import org.geotools.kml.v22.KMLConfiguration;
import org.geotools.xsd.Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.model.EntityElement;
import dz.eadn.sig.model.Layer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour
 *
 */
@Slf4j
@Component
public class KMLWriter implements EntityElementWriter {
	@Autowired
	private Utils utils;

	@Override
	public void writeEntityElements(Layer layer, List<EntityElement> entityElements, OutputStream os) {
		SimpleFeatureCollection featureCollection;
		try {
			featureCollection = utils.getSimpleFeatureCollectionFromEntityElements(layer, entityElements, false, false);
			Encoder encoder = new Encoder(new KMLConfiguration());
			encoder.setIndenting(true);

			encoder.encode(featureCollection, KML.kml, os);
		} catch (SchemaException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public String dataName() {
		// TODO Auto-generated method stub
		return "kml";
	}

	@Override
	public String mimeType() {
		// TODO Auto-generated method stub
		return "application/vnd.google-earth.kml+xml";
	}

	@Override
	public String extension() {
		// TODO Auto-generated method stub
		return ".kml";
	}
}
