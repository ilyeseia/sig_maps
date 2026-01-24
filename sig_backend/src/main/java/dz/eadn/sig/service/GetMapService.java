/**
 * 
 */
package dz.eadn.sig.service;

import java.io.OutputStream;

import dz.eadn.sig.dto.GetMapRequest;
import dz.eadn.sig.model.Map;

/**
 * @author Achrouf Abdenour
 *
 */
public interface GetMapService {
	void getMap(Map map, OutputStream os);

	public void getLayer(GetMapRequest request, OutputStream os);

}
