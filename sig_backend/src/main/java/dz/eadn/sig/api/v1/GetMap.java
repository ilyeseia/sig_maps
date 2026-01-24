/**
 * 
 */
package dz.eadn.sig.api.v1;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dz.eadn.sig.dto.GetMapRequest;
import dz.eadn.sig.model.Map;
import dz.eadn.sig.service.GetMapService;
import dz.eadn.sig.service.MapService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour && LAMOUR Ameur
 *
 */

@RestController
@RequestMapping("/api/v1.0/carto")
@Tag(name = "wms", description = "the wms API")
@Slf4j
public class GetMap {
	private static final String SERVICE_PARM = "SERVICE";
	private static final String WIDTH_PARM = "WIDTH";
	private static final String HEIGHT_PARM = "HEIGHT";
	private static final String REQUEST_PARM = "REQUEST";
	private static final String LAYERS_PARM = "LAYERS";
	private static final String BBOX_PARM = "BBOX";
	private static final String FORMAT_PARM = "FORMAT";
	private static final String VERSION_PARM = "VERSION";
	private static final String CRS_PARM = "SRS";
	private static final String STYLES_PARM = "STYLES";
	private static final String SLD_PARM = "SLD";
	private static final String X_PARM = "X";
	private static final String Y_PARM = "Y";

	@Autowired
	private GetMapService getMapService;

	@Autowired
	private MapService mapService;

	public String getParameter(HttpServletRequest request, String parm) {
		String serviceParm = request.getParameter(parm);

		if (serviceParm == null) {
			serviceParm = request.getParameter(parm.toLowerCase());
		}

		return serviceParm;
	}

	public GetMapRequest buildGetMapRequest(HttpServletRequest request) {
		GetMapRequest getMapRequest = new GetMapRequest();

		getMapRequest.setService(getParameter(request, SERVICE_PARM));
		getMapRequest.setLayers(getParameter(request, LAYERS_PARM));
		getMapRequest.setOriginBbox(getParameter(request, BBOX_PARM));
		getMapRequest.setBbox(getParameter(request, BBOX_PARM).split(","));
		getMapRequest.setWidth(Integer.valueOf(getParameter(request, WIDTH_PARM)));
		getMapRequest.setHeight(Integer.valueOf(getParameter(request, HEIGHT_PARM)));

		getMapRequest.setFormat(getParameter(request, FORMAT_PARM));
		getMapRequest.setStyles(getParameter(request, STYLES_PARM));
		getMapRequest.setSLD(getParameter(request, SLD_PARM));
		getMapRequest.setCrs(getParameter(request, CRS_PARM));
		getMapRequest.setRequest(getParameter(request, REQUEST_PARM));
		getMapRequest.setVersion(getParameter(request, VERSION_PARM));

		return getMapRequest;
	}

	@GetMapping("/wms")
	public void getMap(HttpServletRequest request, HttpServletResponse response) {
		try {
			GetMapRequest getMapRequest = buildGetMapRequest(request);

			response.setContentType("image/png");
			response.setStatus(200);

			getMapService.getLayer(getMapRequest, response.getOutputStream());
		} catch (IOException e) {
			log.error(e.getMessage());
			response.setStatus(500);
		}
	}

	@GetMapping("/map/{id}")
	public void exportTo(@PathVariable("id") String id, HttpServletResponse response) {
		try {
			Map map = mapService.findById(UUID.fromString(id));

			response.setContentType("image/png");
			response.setHeader("Content-Disposition", "attachment; filename=" + map.getSlug() + ".png");
			response.setStatus(200);

			getMapService.getMap(map, response.getOutputStream());
		} catch (IOException e) {
			log.error(e.getMessage());
			response.setStatus(500);
		}
	}

}
