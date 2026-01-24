/**
 * 
 */
package dz.eadn.sig.util;

import java.io.OutputStream;
import java.util.List;

import dz.eadn.sig.model.EntityElement;
import dz.eadn.sig.model.Layer;

/**
 * @author Achrouf Abdenour
 *
 */
public class GML3_2Writer implements EntityElementWriter {

	@Override
	public void writeEntityElements(Layer layer, List<EntityElement> entityElements, OutputStream os) {
		// TODO Auto-generated method stub

	}

	@Override
	public String dataName() {
		// TODO Auto-generated method stub
		return "gml3.2";
	}

	@Override
	public String mimeType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String extension() {
		// TODO Auto-generated method stub
		return ".gml";
	}

}
