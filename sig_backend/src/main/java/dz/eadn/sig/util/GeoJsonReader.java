package dz.eadn.sig.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.feature.simple.SimpleFeatureType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
public class GeoJsonReader implements EntityElementReader {
	@Autowired
	private EntityElementService eeService;

	private SimpleFeatureCollection readFeatureCollection(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int n = 0;
		while ((n = is.read(buf)) >= 0)
			baos.write(buf, 0, n);
		byte[] content = baos.toByteArray();

		InputStream input = new ByteArrayInputStream(content);

		InputStream input2 = new ByteArrayInputStream(content);

		FeatureJSON featureJson = new FeatureJSON();

		SimpleFeatureCollection featureCollection = null;
		SimpleFeatureType simpleFeatureType = (SimpleFeatureType) featureJson.readFeatureCollectionSchema(input, false);
		featureJson.setFeatureType(simpleFeatureType);
		input.close();

		featureCollection = (SimpleFeatureCollection) featureJson.readFeatureCollection(input2);
		input2.close();
		return featureCollection;
	}

	@Override
	public Layer readEntityElements(LayerDto layerDto, InputStream is) {
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
		return "geojson";
	}

}
