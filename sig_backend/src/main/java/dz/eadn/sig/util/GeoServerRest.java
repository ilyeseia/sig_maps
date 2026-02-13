package dz.eadn.sig.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dz.eadn.sig.exceptions.AccessNotPermittedException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;

import dz.eadn.sig.dto.FieldSimpleDto;
import dz.eadn.sig.dto.GeoServerRequest;
import dz.eadn.sig.dto.LayerDto;
import dz.eadn.sig.mapper.CommonModelMapper;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.security.JwtUtils;
import dz.eadn.sig.service.LayerService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
@Component
public class GeoServerRest {

	@Value("${geoserver.wms.url}")
	private String GEOSERVER_WMS;

	@Value("${geoserver.wms.url}")
	private String GEOSERVER_WFS;

	@Value("${geoserver.rest.url}")
	private String GEOSERVER_REST;

	@Value("${geoserver.rest.user}")
	private String GEOSERVER_REST_USER;

	@Value("${geoserver.rest.userGuest}")
	private String GEOSERVER_REST_USER_GUEST;

	@Value("${geoserver.rest.password}")
	private String GEOSERVER_REST_PASSWORD;

	@Value("${geoserver.workspace}")
	private String GEOSERVER_WORKSPACE;

	@Value("${geoserver.datastore}")
	private String GEOSERVER_DATASTORE;

	@Value("${eadn.sig.server}")
	private String SIG_SERVER;

	@Autowired
	private JwtUtils jwtUtils;

	private HttpRequestFactory requestFactory;

	@Autowired
	private LayerService layerService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private CommonModelMapper<?, ?> cModelMapper;

	private static final Logger logger = LoggerFactory.getLogger(GeoServerRest.class);

	@PostConstruct
	public void init() {
		requestFactory = new NetHttpTransport().createRequestFactory();
	}

	private String getCredentials(boolean isGuest) {
		String plainCreds;
		if (isGuest) {
			plainCreds = GEOSERVER_REST_USER_GUEST + ":" + GEOSERVER_REST_PASSWORD;
		} else {
			plainCreds = GEOSERVER_REST_USER + ":" + GEOSERVER_REST_PASSWORD;
		}
		return new String(Base64.encodeBase64(plainCreds.getBytes()));
	}

	private String getFullURL(HttpServletRequest request, String targetRequest) throws UnsupportedEncodingException {
		StringBuilder requestURL = new StringBuilder();
		requestURL.append(targetRequest);

		String queryString = request.getQueryString();
		if (request.getAttribute("exclude") != null) {
			queryString = queryString.replace(request.getAttribute("exclude").toString(), "");
		}
		if (queryString == null) {
			return requestURL.toString();
		} else {
			return requestURL.append('?').append(java.net.URLDecoder.decode(queryString, StandardCharsets.UTF_8.name()))
					.toString();
		}
	}

	private String getWmsFullURL(HttpServletRequest request) throws UnsupportedEncodingException {
		return getFullURL(request, GEOSERVER_WMS);
	}

	private String getWfsFullURL(HttpServletRequest request) throws UnsupportedEncodingException {
		return getFullURL(request, GEOSERVER_WFS);
	}

	public HttpResponse createStyle(LayerDto dto) throws Exception {
		String url = GEOSERVER_REST + "/workspaces/" + GEOSERVER_WORKSPACE + "/styles";

		GeoServerRequest request = new GeoServerRequest();
		request.setUrl(url);
		request.setAccept("application/json");
		request.setContentType("application/vnd.ogc.sld+xml");
		request.setMethod("post");
		request.setBody(dto.getSldBody());

		return buildRestRequest(request);
	}

	public HttpResponse getStyle(String layerSlug) throws Exception {
		String url = GEOSERVER_REST + "/workspaces/" + GEOSERVER_WORKSPACE + "/styles/" + layerSlug;

		GeoServerRequest request = new GeoServerRequest();
		request.setUrl(url);
		request.setAccept("application/vnd.ogc.sld+xml");
		request.setMethod("get");

		return buildRestRequest(request);
	}

	public HttpResponse deleteStyle(String layerSlug) throws Exception {
		String url = GEOSERVER_REST + "/workspaces/" + GEOSERVER_WORKSPACE + "/styles/" + layerSlug;

		GeoServerRequest request = new GeoServerRequest();
		request.setUrl(url);
		request.setAccept("application/json");
		request.setMethod("delete");

		return buildRestRequest(request);
	}

	public HttpResponse updateStyle(LayerDto dto) throws Exception {
		String url = GEOSERVER_REST + "/workspaces/" + GEOSERVER_WORKSPACE + "/styles/" + dto.getSlug();

		GeoServerRequest request = new GeoServerRequest();
		request.setUrl(url);
		request.setAccept("application/json");
		request.setContentType("application/vnd.ogc.sld+xml");
		request.setMethod("put");
		request.setBody(dto.getSldBody());

		return buildRestRequest(request);
	}

	public HttpResponse getFeatureType(String layerSlug) throws Exception {
		String url = GEOSERVER_REST + "/workspaces/limite_admin/datastores/database/featuretypes/" + layerSlug;

		GeoServerRequest request = new GeoServerRequest();
		request.setUrl(url);
		request.setAccept("application/xml");
		request.setMethod("get");
		return buildRestRequest(request);
	}

	public HttpResponse deleteFeatureType(String layerSlug) throws Exception {
		String url = GEOSERVER_REST + "/workspaces/" + GEOSERVER_WORKSPACE + "/datastores/" + GEOSERVER_DATASTORE
				+ "/featuretypes/" + layerSlug;

		GeoServerRequest request = new GeoServerRequest();
		request.setUrl(url);
		request.setMethod("delete");
		request.setAccept("application/json");

		return buildRestRequest(request);
	}

	public HttpResponse updateFeatureType(LayerDto layer) throws Exception {
		String url = GEOSERVER_REST + "/workspaces/" + GEOSERVER_WORKSPACE + "/datastores/" + GEOSERVER_DATASTORE
				+ "/featuretypes/" + layer.getSlug();

		GeoServerRequest request = new GeoServerRequest();
		request.setUrl(url);
		request.setMethod("put");

		String featureType = layer.getFeatureType();
		request.setBody(featureType);
		request.setContentType("application/xml");
		request.setAccept("application/json");

		return buildRestRequest(request);
	}

	public HttpResponse createFeatureType(LayerDto layer) throws Exception {
		String url = GEOSERVER_REST + "/workspaces/" + GEOSERVER_WORKSPACE + "/datastores/" + GEOSERVER_DATASTORE
				+ "/featuretypes";

		GeoServerRequest request = new GeoServerRequest();
		request.setUrl(url);
		request.setMethod("post");

		String featureType = layer.getFeatureType();
		request.setBody(featureType);
		request.setContentType("application/xml");
		request.setAccept("application/json");

		return buildRestRequest(request);
	}

	public HttpResponse buildRestRequest(GeoServerRequest request) throws Exception {
		GenericUrl url = new GenericUrl(request.getUrl());
		HttpRequest req = null;

		switch (request.getMethod()) {
			case "get":
				req = requestFactory.buildGetRequest(url);
				break;
			case "delete":
				req = requestFactory.buildDeleteRequest(url);
				break;
			case "put":
			case "post":
				req = requestFactory.buildRequest(request.getMethod(), new GenericUrl(request.getUrl()),
						ByteArrayContent.fromString(request.getContentType(), request.getBody()));
				req.getHeaders().setContentType(request.getContentType());
				break;
			default:
				throw new Exception("Unsuppored method");
		}

		req.getHeaders().setAccept(request.getAccept());
		// Admin requests always use admin credentials (isGuest = false)
		req.getHeaders().setAuthorization("Basic " + getCredentials(false));

		return req.execute();
	}

	public HttpResponse buildRestRequest(GeoServerRequest request, HttpServletResponse response) throws IOException {
		HttpRequest req = requestFactory.buildRequest(request.getMethod(), new GenericUrl(request.getUrl()),
				ByteArrayContent.fromString(request.getContentType(), request.getBody()));
		req.getHeaders().setContentType(request.getContentType());
		req.getHeaders().setAccept(request.getAccept());

		req.getHeaders().setAuthorization("Basic " + getCredentials(false));

		return req.execute();
	}

	public void wfs(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException, IOException {
		String token = request.getParameter("token");
		if (token != null && jwtUtils.validateJwtToken(token)) {
			HttpRequest req = requestFactory.buildGetRequest(new GenericUrl(getWfsFullURL(request)));
			// WFS uses admin/standard credentials
			req.getHeaders().setAuthorization("Basic " + getCredentials(false));
			HttpResponse res = req.execute();

			response.setContentType(request.getParameter("format"));
			IOUtils.copy(res.getContent(), response.getOutputStream());
		} else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	public void wms(HttpServletRequest request, HttpServletResponse response, boolean status, boolean isGuest)
			throws UnsupportedEncodingException, IOException {
		try {
			String layers = request.getParameter("LAYERS");
			String map = request.getParameter("MAP");
			if (layers != null) {
				String[] splitedLayer = layers.split(",");
				if (splitedLayer.length > 0) {
					String layerSlug;
					String excludedLayer;
					for (int i = 0; i < splitedLayer.length; i++) {
						layerSlug = splitedLayer[i].split(":")[1];
						if (layerSlug != null) {
							try {
								layerService.CheckIfUserHasPrivilegeOnLayer(layerSlug, map,
										"ENTITY_ELEMENT_READ_AUTHORITY", "read");
							} catch (AccessNotPermittedException e) {
								if (request.getAttribute("exclude") != null) {
									excludedLayer = i == 1 ? "%2C" : "";
									excludedLayer += splitedLayer[i].replace(":", "%3A");
									if (i < splitedLayer.length - 1) {
										excludedLayer += "%2C";
									}
									request.setAttribute("exclude", request.getAttribute("exclude") + excludedLayer);
									if (i == splitedLayer.length - 1) {
										throw new AccessNotPermittedException(
												"Vous ne disposez pas de suffisamment de privilèges pour effectuer cette action !");
									}

								} else {
									request.setAttribute("exclude", splitedLayer[i].replace(":", "%3A"));
								}
							}
						}
					}
				} else if (layers.split(":")[1] != null) {
					layerService.CheckIfUserHasPrivilegeOnLayer(layers.split(":")[1], map,
							"ENTITY_ELEMENT_READ_AUTHORITY", "read");
				}
			}
			HttpRequest req = requestFactory.buildGetRequest(new GenericUrl(getWmsFullURL(request)));
			req.getHeaders().setAuthorization("Basic " + getCredentials(isGuest));

			HttpResponse res = req.execute();
			response.setContentType(request.getParameter("format"));

			String type = request.getParameter("REQUEST");

			if (!Objects.isNull(type) && type.equals("GetFeatureInfo")) {
				FeatureCollection fc = null;
				String content = res.parseAsString();
				if (status == true) {
					fc = filterDataFromGeoServer(FeatureCollection.fromJson(content), true);
				}

				else {
					fc = filterDataFromGeoServer(FeatureCollection.fromJson(content), false);
				}

				InputStream is = IOUtils.toInputStream(fc.toJson(), StandardCharsets.UTF_8);
				IOUtils.copy(is, response.getOutputStream());
			} else {
				IOUtils.copy(res.getContent(), response.getOutputStream());
			}
		} catch (IOException e) {
			log.info(e.getMessage());
		}
	}

	public void securedWms(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException, IOException {
		String token = request.getParameter("token");
		if (token != null && jwtUtils.validateJwtToken(token)) {
			try {
				wms(request, response, true, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	public void publicWms(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException, IOException {
		try {
			wms(request, response, false, true);
		} catch (Exception e) {
			log.error("Error in publicWms", e);
		}
	}

	public FeatureCollection filterDataFromGeoServer(FeatureCollection fc, boolean authenticated) {

		try {

			List<Feature> features = fc.getFeatures();

			if (features != null) {
				for (Feature feature : features) {

					UUID layerId = null;
					// map JsonObject to Map
					Map<String, String> properties = new Gson().fromJson(feature.getProperties(), Map.class);
					String uuid = properties.get("layer_entity_element");
					if (uuid != null && uuid
							.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")) {
						layerId = UUID.fromString(uuid);
						Layer layer = layerService.findById(layerId);

						if (layer != null) {

							List<FieldSimpleDto> fields = (List<FieldSimpleDto>) cModelMapper.mapList(layer.getFields(),
									FieldSimpleDto.class);
							if (authenticated == true) {

								List<String> fieldsName = fields.stream().filter(field -> field.getVisible())
										.map(field -> field.getSlug()).collect(Collectors.toList());

								fieldsName.add("id");

								properties = properties.entrySet().stream()
										.filter(property -> fieldsName.contains(property.getKey()))
										.collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));

								properties.put("layer_entity_element", layerId.toString());

							} else {

								List<String> fieldsName = fields.stream().filter(field -> field.getVisible())
										.filter(field -> field.getPublique()).map(field -> field.getSlug())
										.collect(Collectors.toList());

								properties = properties.entrySet().stream()
										.filter(map -> fieldsName.contains(map.getKey()))
										.collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));

								properties.put("layer_entity_element", layerId.toString());
							}

							feature.setProperties(new Gson().toJsonTree(properties).getAsJsonObject());
						}

					}

				}
			}
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		return fc;
	}
}
