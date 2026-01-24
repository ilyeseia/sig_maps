package dz.eadn.sig.util;

import java.io.InputStream;

import dz.eadn.sig.dto.LayerDto;
import dz.eadn.sig.model.Layer;

/**
 * @author Achrouf Abdenour
 *
 */
public interface EntityElementReader {
	Layer readEntityElements(LayerDto layerDto, InputStream is);

	String dataName();
}
