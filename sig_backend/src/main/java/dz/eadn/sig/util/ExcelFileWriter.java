package dz.eadn.sig.util;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.model.EntityElement;
import dz.eadn.sig.model.Field;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.service.FieldService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ameur LAMOUR
 *
 */

@Slf4j
@Component
public class ExcelFileWriter implements EntityElementWriter {

	private Workbook workbook;
	private Sheet sheet;
	@Autowired
	FieldService fieldService;

	@Override
	public void writeEntityElements(Layer layer, List<EntityElement> entityElements, OutputStream os) {

		try {

			workbook = new XSSFWorkbook();

			sheet = workbook.createSheet(layer.getSlug());

			createHeader(layer);
			createRows(entityElements, layer.getFields());

			workbook.write(os);

			workbook.close();

		} catch (IOException e) {
			log.info(e.getMessage());
		}

	}

	private void createCellHeader(Row row, int cellNum, String value) {

		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.DIAMONDS);

		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		headerStyle.setFont(font);

		Cell cell = row.createCell(cellNum);
		cell.setCellValue(value);
		cell.setCellStyle(headerStyle);
	}

	private void createHeader(Layer layer) {

		Row row = sheet.createRow(0);

		for (Field field : layer.getFields()) {

			if (row.getLastCellNum() > 0)
				createCellHeader(row, row.getLastCellNum(), field.getName());
			else
				createCellHeader(row, 0, field.getName());

		}

		if (layer.getTopo().equals("Point")) {

			createCellHeader(row, row.getLastCellNum(), "Longitude");
			createCellHeader(row, row.getLastCellNum(), "Latitude");

		}

	}

	private void createCellRow(Row row, int cellNum, String value) {

		CellStyle rowStyle = workbook.createCellStyle();
		rowStyle.setWrapText(true);

		Cell cell = row.createCell(cellNum);
		cell.setCellValue(value);
		cell.setCellStyle(rowStyle);

	}

	private void createRows(List<EntityElement> entityElements, List<Field> fields) {

		int rowNum = 1;

		for (EntityElement entityElement : entityElements) {

			Row row = sheet.createRow(rowNum);

			Map<String, String> properties = entityElement.getProperties();

			for (Field field : fields) {

				if (row.getLastCellNum() > 0) {
					createCellRow(row, row.getLastCellNum(), properties.get(field.getSlug()));
				}

				else {
					createCellRow(row, 0, properties.get(field.getSlug()));
				}

			}

			if (isPoint(entityElement.getGeom())) {

				createCellRow(row, row.getLastCellNum(), ((Point) entityElement.getGeom()).getX() + "");
				createCellRow(row, row.getLastCellNum(), ((Point) entityElement.getGeom()).getY() + "");
			}

			rowNum++;

		}

	}

	private boolean isPoint(Geometry geometry) {

		if (geometry instanceof Point)
			return true;

		return false;
	}

	@Override
	public String dataName() {
		return "excel";
	}

	@Override
	public String mimeType() {
		return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	}

	@Override
	public String extension() {
		return ".xlsx";
	}

}
