package dz.eadn.sig.service;

import dz.eadn.sig.constants.Constants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import dz.eadn.sig.config.FeignClientConfiguration;


/**
 * @Author: C.LOKBANI
 */
@FeignClient(name = "geoserver", url = "${geoserver.rest.url}" +
		"/workspaces/" + Constants.GEO_SERVER_WORKSPACE +
		"/", configuration = FeignClientConfiguration.class)
public interface GeoserverService {


	@PostMapping(value = "datastores/database/featuretypes", consumes = "application/xml", produces = "application/xml")
	void addLayer(@RequestBody String data);

	@GetMapping(value = "datastores/database/featuretypes/{layerSlug}", consumes = "application/xml", produces = "application/xml")
	String getLayer(@PathVariable(value = "layerSlug") String layerSlug);

	@PutMapping(value = "datastores/database/featuretypes/{featureType}", consumes = "application/xml", produces = "application/xml")
	void updateLayer(@PathVariable(value = "featureType") String featureType, @RequestBody String data);

	@DeleteMapping(value = "datastores/database/featuretypes/{layerSlug}?recurse=true", consumes = "application/json")
	void deleteLayer(@PathVariable(value = "layerSlug") String layerSlug);

	@DeleteMapping(value = "coveragestores/{layerSlug}?purge=true&recurse=true", consumes = "application/json")
	void deleteCoverageStore(@PathVariable(value = "layerSlug") String layerSlug);

	@PostMapping(value = "styles", consumes = "application/vnd.ogc.sld+xml", produces = "application/json")
	void addStyle(@RequestBody String sldStyle);

	@PutMapping(value = "styles/{styleName}", consumes = "application/vnd.ogc.sld+xml", produces = "application/json")
	void updateStyle(@PathVariable(value = "styleName") String styleName, @RequestBody String sldStyle);

	@GetMapping(value = "styles/{styleName}", produces = "application/vnd.ogc.sld+xml")
	String getStyle(@PathVariable (value = "styleName") String styleName);

	@DeleteMapping(value = "styles/{layerSlug}", consumes = "application/json")
	void deleteStyle(@PathVariable(value = "layerSlug") String layerSlug);

}
