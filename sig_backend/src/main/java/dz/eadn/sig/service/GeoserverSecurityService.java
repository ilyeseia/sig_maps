package dz.eadn.sig.service;

import dz.eadn.sig.config.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;

/**
 * @Author: C.LOKBANI
 */
@FeignClient(name = "geoserverSecurity", url = "${geoserver.rest.url}"
		+ "/", configuration = FeignClientConfiguration.class)
public interface GeoserverSecurityService {

	@PostMapping(value = "security/acl/layers", produces = "application/json", consumes = "application/json")
	void addLayersRules(@RequestBody HashMap<String, String> rules);

	@DeleteMapping(value = "security/acl/layers/{rule}", produces = "application/json", consumes = "application/json")
	void deleteLayersRules(@PathVariable(value = "rule") String rule);

}
