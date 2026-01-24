package dz.eadn.sig.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.*;
import java.util.stream.Collectors;

import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.constants.Constants;
import dz.eadn.sig.model.EntityElement;
import dz.eadn.sig.model.Field;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.Settings;
import dz.eadn.sig.service.SettingsService;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

/**
 * @author Achrouf Abdenour
 *
 */
@Slf4j
@Component
public class Utils {

	@Autowired
	protected DataSource dataSource;


	@Autowired
	private SettingsService settingsService;

	public Geometry parseWKT(String wellKnownText) throws org.locationtech.jts.io.ParseException {
		WKTReader wktReader = new WKTReader();
		return wktReader.read(wellKnownText);
	}


	public String writeGeometryToGeoJson(Geometry geometry) {
		GeometryJSON json = new GeometryJSON();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			json.write(geometry, baos);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return baos.toString();
	}

	public String randomString() {
		String alphabetsInUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String alphabetsInLowerCase = "abcdefghijklmnopqrstuvwxyz";
		// create a super set of all characters
		String allCharacters = alphabetsInLowerCase + alphabetsInUpperCase;
		// initialize a string to hold result
		StringBuffer randomString = new StringBuffer();
		// loop for 10 times
		for (int i = 0; i < 10; i++) {
			// generate a random number between 0 and length of all characters
			int randomIndex = (int)(Math.random() * allCharacters.length());
			// retrieve character at index and add it to result
			randomString.append(allCharacters.charAt(randomIndex));
		}
		return randomString.toString();
	}

	public String writeEntityElementToJson(EntityElement entityElement, boolean slug, boolean exportSystemFields) {
		SimpleFeature sf = null;
		try {
			sf = entityElementToFeature(entityElement, slug, exportSystemFields);
		} catch (SchemaException e1) {
			log.error(e1.getMessage());
		}
		FeatureJSON json = new FeatureJSON();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			json.writeFeature(sf, baos);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return baos.toString();
	}

	public Geometry readGeometryFromGeoJson(String json) throws IOException {
		GeometryJSON gjson = new GeometryJSON();
		return gjson.read(json);
	}

	public static boolean isProbablyArabic(String s) {
		for (int i = 0; i < s.length();) {
			int c = s.codePointAt(i);
			if (c >= 0x0600 && c <= 0x06E0)
				return true;
			i += Character.charCount(c);
		}
		return false;
	}

	public static String toSlug(String input) {
		String nowhitespace = Constants.WHITESPACE.matcher(input.trim()).replaceAll("_");
		String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
		if(normalized.matches(String.valueOf(Constants.NONLATIN))){
			String slug = Constants.NONLATIN.matcher(normalized).replaceAll("");
			return slug.toLowerCase(Locale.ENGLISH).replaceAll("[^\\p{ASCII}]", "").replaceAll("'", "");
		}else if(isProbablyArabic(input)){
			return normalized.toLowerCase().replaceAll("'", "");
		}
		else{
			return normalized.toLowerCase().replaceAll("[^\\p{ASCII}]", "").replaceAll("'", "");
		}
	}

	public SimpleFeatureCollection getSimpleFeatureCollectionFromEntityElements(Layer layer,
			List<EntityElement> entityElements, boolean slug, boolean exportSystemFields) throws SchemaException {
		if (layer == null) {
			SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
			builder.setName("featureCollection");
			return new DefaultFeatureCollection(null, builder.buildFeatureType());
		}

		DefaultFeatureCollection fc;
		List<SimpleFeature> features = entityElementsToFeatures(layer, entityElements, slug, exportSystemFields);
		if (features.isEmpty()) {
			fc = new DefaultFeatureCollection(layer.getSlug(), createSchemaFromLayer(layer, slug, exportSystemFields));
		} else {
			fc = new DefaultFeatureCollection(layer.getSlug());
			fc.addAll(features);
		}
		return fc;
	}

	public List<SimpleFeature> entityElementsToFeatures(Layer layer, List<EntityElement> entityElements, boolean slug,
			boolean exportSystemFields) throws SchemaException {
		SimpleFeatureType sft = createSchemaFromLayer(layer, slug, exportSystemFields);
		final SimpleFeatureBuilder sm = new SimpleFeatureBuilder(sft);

		Map<String, String> fields = createFieldNameSlugMap(layer);
		return entityElements.stream()
				.map(entityElement -> entityElementToFeature(sm, entityElement, fields, slug, exportSystemFields))
				.collect(Collectors.toList());
	}

	public SimpleFeatureType createSchemaFromLayer(Layer layer, boolean slug, boolean exportSystemFields)
			throws SchemaException {
		StringBuilder sb = new StringBuilder();

		if (layer.getFields() != null) {
			for (Field field : layer.getFields()) {
				if (slug)
					sb.append(field.getSlug() + ":" + field.getType().getJavaClass() + ",");
				else
					sb.append(field.getName() + ":" + field.getType().getJavaClass() + ",");
			}
		}

		if (exportSystemFields) {
			sb.append("createdBy:String,");
			sb.append("createDate:java.util.Date,");
			sb.append("modifiedBy:String,");
			sb.append("modifiedDate:java.util.Date,");
		}

		sb.append("the_geom:" + layer.getTopo());
		return DataUtilities.createType(layer.getName(), sb.toString());
	}

	public Map<String, String> getFieldUUIDNameMap(Layer layer) {
		Map<String, String> fields = new HashMap<String, String>();

		for (Field field : layer.getFields()) {
			fields.put(field.getId().toString(), field.getName());
		}

		return fields;
	}

	public Map<String, String> getFieldNameUUIDMap(Layer layer) {
		Map<String, String> fields = new HashMap<String, String>();

		for (Field field : layer.getFields()) {
			fields.put(field.getName(), field.getId().toString());
		}

		return fields;
	}

	public Map<String, Field> getUUIDFieldMap(Layer layer) {
		Map<String, Field> fields = new HashMap<String, Field>();

		for (Field field : layer.getFields()) {
			fields.put(field.getId().toString(), field);
		}

		return fields;
	}

	public static Map<String, String> createFieldNameSlugMap(Layer layer) {
		Map<String, String> fields = new HashMap<String, String>();

		for (Field field : layer.getFields()) {
			fields.put(field.getSlug(), field.getName());
		}

		return fields;
	}

	public SimpleFeature entityElementToFeature(EntityElement entityElement, boolean slug, boolean exportSystemFields)
			throws SchemaException {
		Layer layer = entityElement.getLayer();
		SimpleFeatureType sft = createSchemaFromLayer(layer, slug, exportSystemFields);
		final SimpleFeatureBuilder sm = new SimpleFeatureBuilder(sft);

		Map<String, String> fields = createFieldNameSlugMap(layer);

		return entityElementToFeature(sm, entityElement, fields, slug, exportSystemFields);
	}

	public String getFileUrl(String fileName) {
		Settings serverAddress = settingsService.findByCode(Constants.SERVER_ADDRESS_CODE);
		String controllerUrl = serverAddress.getValue() + Constants.fileDownloadServer;
		String imageUrl = controllerUrl + fileName;
		return imageUrl;
	}

	public String getFileUrl(String folderName,  String fileName) {
		Settings serverAddress = settingsService.findByCode(Constants.SERVER_ADDRESS_CODE);
		String controllerUrl = serverAddress.getValue() + Constants.fileDownloadServer + folderName;
		String imageUrl = controllerUrl + fileName;
		return imageUrl;
	}
/**
 * Cette methode return le path d'un dossier
 * @param folderName
 * @return String path
 */
	public String getFolderUrl(String folderName) {
		Settings serverAddress = settingsService.findByCode(Constants.SERVER_ADDRESS_CODE);
		String controllerUrl = serverAddress.getValue() + Constants.fileDownloadServer;
		String imageUrl = controllerUrl + folderName;
		return imageUrl;
	}

	public SimpleFeature entityElementToFeature(SimpleFeatureBuilder sfb, EntityElement entityElement,
			Map<String, String> fields, boolean slug, boolean exportSystemFields) {
		SimpleFeature simpleFeature = sfb.buildFeature(entityElement.getId().toString());

		for (Map.Entry<String, String> prop : entityElement.getProperties().entrySet()) {
			try{
				if (slug) {
					simpleFeature.setAttribute(prop.getKey(), prop.getValue());
				} else {
					simpleFeature.setAttribute(fields.get(prop.getKey()), prop.getValue());
				}
			}catch (IllegalAttributeException e){
				e.printStackTrace();
			}
		}

		if (exportSystemFields) {
			simpleFeature.setAttribute("createdBy", entityElement.getCreatedBy());
			simpleFeature.setAttribute("createDate", entityElement.getCreateDate());
			simpleFeature.setAttribute("modifiedBy", entityElement.getModifiedBy());
			simpleFeature.setAttribute("modifiedDate", entityElement.getLastModifiedDate());
		}

		if (entityElement.getGeom() != null)
			simpleFeature.setDefaultGeometry(entityElement.getGeom());

		return simpleFeature;
	}

}
