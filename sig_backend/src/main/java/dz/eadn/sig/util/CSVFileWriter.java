package dz.eadn.sig.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import dz.eadn.sig.model.EntityElement;
import dz.eadn.sig.model.Field;
import dz.eadn.sig.model.Layer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour
 *
 */
@Component
@Slf4j
public class CSVFileWriter implements EntityElementWriter {
	private CsvSchema createSchemaFromLayer(Layer layer, boolean exportSystemFields) {
		CsvSchema.Builder builder = CsvSchema.builder();

		for (Field field : layer.getFields()) {
			builder.addColumn(field.getName());
		}

		if (exportSystemFields) {
			builder.addColumn("createdBy");
			builder.addColumn("createDate");
			builder.addColumn("modifiedBy");
			builder.addColumn("modifiedDate");
		}

		builder.addColumn("the_geom");
		return builder.setUseHeader(true).build();
	}

	private Map<String, String> processEntityElement(EntityElement entityElement, Layer layer,
			boolean exportSystemFields) {
		Map<String, String> map = new HashMap<>();

		Map<String, String> props = entityElement.getProperties();
		for (Field field : layer.getFields()) {
				map.put(field.getName(), props.get(field.getSlug()) != null ? props.get(field.getSlug()) : "");
		}
		if (exportSystemFields) {
			map.put("createdBy", entityElement.getCreatedBy());
			map.put("createDate", entityElement.getCreateDate().toString());
			map.put("modifiedBy", entityElement.getModifiedBy());
			map.put("modifiedDate", entityElement.getLastModifiedDate().toString());
		}

		map.put("the_geom", entityElement.getGeom().toText());
		return map;
	}

	private List<Map<String, String>> processEntityElements(List<EntityElement> entityElements, Layer layer) {
		return entityElements.stream().filter(e -> e.getGeom() != null).map(e -> processEntityElement(e, layer, false)).collect(Collectors.toList());
	}

	@Override
	public void writeEntityElements(Layer layer, List<EntityElement> entityElements, OutputStream os) {
		CsvSchema schema = createSchemaFromLayer(layer, false);
		CsvMapper mapper = new CsvMapper();
		ObjectWriter objectWriter = mapper.writer(schema);
		try {
			objectWriter.writeValue(os, processEntityElements(entityElements, layer));
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public String dataName() {
		return "csv";
	}

	@Override
	public String mimeType() {
		return "text/plain; charset=utf-8";
	}

	@Override
	public String extension() {
		return ".csv";
	}
}
