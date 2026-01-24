package dz.eadn.sig.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import dz.eadn.sig.exceptions.GlobalException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType;

import dz.eadn.sig.dto.FieldDto;
import dz.eadn.sig.dto.LayerDto;
import dz.eadn.sig.mapper.LayerMapper;
import dz.eadn.sig.model.EntityElement;
import dz.eadn.sig.model.Field;
import dz.eadn.sig.model.FieldType;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.repository.EntityElementRepository;
import dz.eadn.sig.service.LayerService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour
 *
 */
@Component
@Slf4j
public class CSVFileReader implements EntityElementReader {
	@Autowired
	private Utils utils;

	@Autowired
	private LayerService layerService;

	@Autowired
	private LayerMapper layerMapper;

	@Autowired
	private EntityElementRepository entityElementRepository;

	private EntityElement readEntityElement(Layer layer, Map<String, String> elements, Map<String, String> fieldsNameUUID) {
		EntityElement entityElement = new EntityElement();
		entityElement.setLayer(layer);
		Map<String, String> props = new HashMap<>();

		for (Map.Entry<String, String> element : elements.entrySet()) {
			String propName = element.getKey();

			if (propName.equals("id"))
				continue;
			if(element.getValue() == null || element.getValue() == "") {
				props.put(propName, "");
				continue;
			}
			try {
				Geometry geom = utils.parseWKT(element.getValue());
				if(geom == null) throw new ParseException("exception");
				geom.setSRID(4326);
				entityElement.setGeom(geom);
			} catch (org.locationtech.jts.io.ParseException e) {
				props.put(propName, element.getValue());
			}
		}

		entityElement.setProperties(props);
		return entityElement;
	}

	// automatic field matching ,not use for now
	public FieldType getFieldType(ColumnType columnType) {
		switch (columnType) {
		case STRING:
			return FieldType.TEXT;
		case NUMBER:
			return FieldType.NUMBER;
		case BOOLEAN:
			return FieldType.SELECT;
		case ARRAY:
			return FieldType.MULTI_SELECT;
		default:
			return FieldType.TEXT;
		}
	}

	@Override
	public Layer readEntityElements(LayerDto layerDto, InputStream is) {
		CsvMapper mapper = new CsvMapper();
		// mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		CsvSchema schema = CsvSchema.emptySchema().withHeader();

		MappingIterator<LinkedHashMap<String, String>> it = null;
		try {
			it = mapper.readerFor(LinkedHashMap.class).with(schema).readValues(is);
		} catch (IOException e1) {
			log.error(e1.getMessage());
		}

		LinkedHashMap<String, String> firstElement = it.next();

		/*
		 * Layer layer = new Layer(); layer.setName(layerDto.getName());
		 * layer.setSlug(Utils.toSlug(layerDto.getName()));
		 * layer.setStyle(layerDto.getStyle());
		 */

		List<FieldDto> layerFields = new ArrayList<>();

		for (Map.Entry<String, String> entry : firstElement.entrySet()) {
			String propName = entry.getKey();

			if (propName.equals("id"))
				continue;
//			if(entry.getValue() == null || entry.getValue() == "") {
//				continue;
//			}
			try {
				Geometry geom = utils.parseWKT(entry.getValue());
				if(geom == null) throw new ParseException("exception");
				layerDto.setTopo(geom.getGeometryType());
			} catch (org.locationtech.jts.io.ParseException e) {
				FieldDto fDto = new FieldDto();
				fDto.setRequired(false);
				fDto.setVisible(true);

				try {
					Double.parseDouble(entry.getValue());
					fDto.setType(FieldType.NUMBER);
				} catch (NumberFormatException nfe) {
					fDto.setType(FieldType.TEXT);
				}

				fDto.setName(propName);
				layerFields.add(fDto);
			}
		}

		if (!layerFields.isEmpty()) {
			layerDto.setIdentifiant(Utils.toSlug(layerFields.get(0).getName()));
		}

		layerDto.setFieldDtos(layerFields);
		LayerDto dto = null;
		try {

			dto = layerService.save(layerDto);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		Layer entityLayer = layerMapper.dtoToEntity(dto);
		Map<String, String> fieldsNameUUID = utils.getFieldNameUUIDMap(entityLayer);

		LinkedHashMap<String, String> element2 = new LinkedHashMap<>();
		for (Map.Entry<String,String> entry : firstElement.entrySet())
			element2.put(Utils.toSlug(entry.getKey()), entry.getValue());
		entityElementRepository.save(readEntityElement(entityLayer,element2, fieldsNameUUID));
		while (it.hasNext()) {
			LinkedHashMap<String, String> element = it.next();
			LinkedHashMap<String, String> element1 = new LinkedHashMap<>();
			for (Map.Entry<String,String> entry : element.entrySet())
				element1.put(Utils.toSlug(entry.getKey()), entry.getValue());
			entityElementRepository.save(readEntityElement(entityLayer, element1, fieldsNameUUID));
		}


		return entityLayer;
	}

	@Override
	public String dataName() {
		return "csv";
	}
}