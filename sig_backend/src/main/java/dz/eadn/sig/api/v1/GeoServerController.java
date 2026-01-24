package dz.eadn.sig.api.v1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.http.HttpResponse;

import dz.eadn.sig.util.GeoServerRest;

@RestController
@RequestMapping("/api/v1.0/geoserver")
public class GeoServerController {

	@Autowired
	private GeoServerRest geoServerRest;


	@GetMapping("/styles/{layer}")
	public void getStyle(@PathVariable("layer") String layerSlug, HttpServletResponse response) throws Exception {
		HttpResponse res = geoServerRest.getStyle(layerSlug);
		response.setContentType("application/vnd.ogc.sld+xml");
		IOUtils.copy(res.getContent(), response.getOutputStream());
	}

	@GetMapping("/wfs")
	public void wfs(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException, IOException {
		geoServerRest.wfs(request, response);
	}

	@GetMapping("/wms")
	public void wms(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException, IOException {
		geoServerRest.securedWms(request, response);
	}

	@GetMapping("/public/wms")
	public void publicWms(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException, IOException {
		geoServerRest.publicWms(request, response);
	}

}
