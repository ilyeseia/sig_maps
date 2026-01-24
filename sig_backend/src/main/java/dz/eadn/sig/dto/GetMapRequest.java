/**
 * 
 */
package dz.eadn.sig.dto;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour
 *
 */

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetMapRequest {
	private String service;
	private String request;
	private String format;
	private boolean transparent;
	private String layers;
	private String[] bbox;
	private String originBbox;
	private String crs;
	private int height;
	private int width;
	private String styles;
	private String SLD;
	private String version;
	private String viewparams;

	public ReferencedEnvelope getOriginalEnvelope() {
		ReferencedEnvelope env = null;

		CoordinateReferenceSystem sourceCrs = null;
		try {
			sourceCrs = CRS.decode(crs);
			env = new ReferencedEnvelope(Double.valueOf(bbox[0]), Double.valueOf(bbox[2]), Double.valueOf(bbox[1]),
					Double.valueOf(bbox[3]), sourceCrs);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return env;
	}

	public ReferencedEnvelope getReferencedEnvelope() {
		ReferencedEnvelope env = null;

		CoordinateReferenceSystem sourceCrs = null;
		CoordinateReferenceSystem targetCrs = null;
		try {
			sourceCrs = CRS.decode(crs);
			targetCrs = CRS.decode("EPSG:4326");
			env = new ReferencedEnvelope(Double.valueOf(bbox[0]), Double.valueOf(bbox[2]), Double.valueOf(bbox[1]),
					Double.valueOf(bbox[3]), sourceCrs).transform(targetCrs, true);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return env;
	}
}
