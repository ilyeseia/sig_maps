/**
 * 
 */
package dz.eadn.sig.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.wfs.GML;
import org.geotools.wfs.GML.Version;
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
public class GML3Writer implements EntityElementWriter {

	@Autowired
	private Utils utils;

	@Override
	public void writeEntityElements(Layer layer, List<EntityElement> entityElements, OutputStream os) {
		SimpleFeatureCollection featureCollection;
		try {
			featureCollection = utils.getSimpleFeatureCollectionFromEntityElements(layer, entityElements, false, false);
			GML encode = new GML(Version.WFS1_1);
			encode.setNamespace("Location", "http://localhost/Location.xsd");
			encode.setLegacy(true);
			encode.encode(os, featureCollection);
		} catch (SchemaException e1) {
			log.error(e1.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}

	}

	@Override
	public String dataName() {
		// TODO Auto-generated method stub
		return "gml3";
	}

	@Override
	public String mimeType() {
		// TODO Auto-generated method stub
		return "application/vnd.ogc.gml";
	}

	@Override
	public String extension() {
		// TODO Auto-generated method stub
		return ".gml";
	}

}
