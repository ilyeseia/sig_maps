package dz.eadn.sig.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.geojson.feature.FeatureJSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.model.EntityElement;
import dz.eadn.sig.model.Layer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour
 *
 */
@Component
@Slf4j
public class GeoJsonWriter implements EntityElementWriter {
	@Autowired
	private Utils utils;

	private void writeFeatureCollection(SimpleFeatureCollection fc, OutputStream os) throws IOException {
		FeatureJSON featureJson = new FeatureJSON();
		featureJson.writeFeatureCollection(fc, os);
		os.flush();
	}

	@Override
	public void writeEntityElements(Layer layer, List<EntityElement> entityElements, OutputStream os) {
		try {
			writeFeatureCollection(
					utils.getSimpleFeatureCollectionFromEntityElements(layer, entityElements, false, false), os);
		} catch (IOException | SchemaException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public String dataName() {
		return "geojson";
	}

	@Override
	public String mimeType() {
		return "application/json";
	}

	@Override
	public String extension() {
		return ".geojson";
	}
}
