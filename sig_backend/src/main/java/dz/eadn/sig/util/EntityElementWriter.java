package dz.eadn.sig.util;

import java.io.OutputStream;
import java.util.List;

import dz.eadn.sig.model.EntityElement;
import dz.eadn.sig.model.Layer;

/**
 * @author Achrouf Abdenour
 *
 */
public interface EntityElementWriter {
	void writeEntityElements(Layer layer, List<EntityElement> entityElements, OutputStream os);

	String dataName();

	String mimeType();

	String extension();
}
