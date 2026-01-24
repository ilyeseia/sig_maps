package dz.eadn.sig.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import dz.eadn.sig.model.EntityElement;
import dz.eadn.sig.model.Field;
import dz.eadn.sig.model.Layer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ameur LAMOUR
 *
 */
@Slf4j
@Component
public class PDFFileWriter implements EntityElementWriter {

	@Override
	public void writeEntityElements(Layer layer, List<EntityElement> entityElements, OutputStream os) {
		try {

			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, os);
			Rotate event = new Rotate();
			writer.setPageEvent(event);
			document.open();

			// event.setOrientation(PdfPage.PORTRAIT);

			Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
			/*
			 * if (layer.getIconUrl() != null) {
			 * 
			 * Image logo =
			 * Image.getInstance(uploadFileService.loadFile(layer.getIconUrl()));
			 * 
			 * logo.setAlignment(Image.ALIGN_LEFT); logo.setWidthPercentage(10);
			 * document.add(logo); }
			 */

			Paragraph title = new Paragraph("LA LISTE DES : " + layer.getSlug().toUpperCase(), font);
			title.setAlignment(Paragraph.ALIGN_LEFT);
			document.add(title);

			document.add(createTable(layer, entityElements));
			document.close();

		} catch (Exception e) {
			log.info(e.getMessage());
		}

	}

	private PdfPTable createTable(Layer layer, List<EntityElement> entityElements) {

		PdfPTable table = new PdfPTable(layer.getFields().size() + 4);
		table.setWidthPercentage(100);
		table.setSpacingBefore(20);
//		table.setWidths(new int[] { 1, 3, 3, 2, 3 });

		addHeader(table, layer.getFields());
		try {
			addRows(table, entityElements, layer.getFields());
		} catch (BadElementException e) {
			log.info(e.getMessage());
		} catch (URISyntaxException e) {
			log.info(e.getMessage());
		} catch (IOException e) {
			log.info(e.getMessage());
		}

		return table;

	}

	private PdfPCell createHeaderCell(String value) {

		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBorderWidth(2);
		cell.setPhrase(new Phrase(value));

		return cell;

	}

	private PdfPCell createRowCell(String value) {

		PdfPCell cell = new PdfPCell();
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPhrase(new Phrase(value));

		return cell;

	}

	private void addHeader(PdfPTable table, List<Field> fields) {

		for (Field field : fields) {

			table.addCell(createHeaderCell(field.getSlug()));

		}

		table.addCell(createHeaderCell("Creer par"));
		table.addCell(createHeaderCell("Creer le"));
		table.addCell(createHeaderCell("Modifie par"));
		table.addCell(createHeaderCell("Modifie le"));

	}

	private void addRows(PdfPTable table, List<EntityElement> entityElements, List<Field> fields)
			throws URISyntaxException, BadElementException, IOException {

		entityElements.stream().forEach(entityElement -> {

			Map<String, String> properties = entityElement.getProperties();

			for (Field field : fields) {

				table.addCell(createRowCell(properties.get(field.getSlug())));

			}

			table.addCell(createRowCell(entityElement.getCreatedBy()));
			table.addCell(
					createRowCell(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(entityElement.getCreateDate())));
			table.addCell(createRowCell(entityElement.getModifiedBy()));
			table.addCell(createRowCell(
					new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(entityElement.getLastModifiedDate())));

		});
	}

	@Override
	public String dataName() {
		return "pdf";
	}

	@Override
	public String mimeType() {
		return "application/pdf";
	}

	@Override
	public String extension() {
		return ".pdf";
	}

}

class Rotate extends PdfPageEventHelper {

	protected PdfNumber orientation = PdfPage.PORTRAIT;

	public void setOrientation(PdfNumber orientation) {
		this.orientation = orientation;
	}

	@Override
	public void onStartPage(PdfWriter writer, Document document) {
		writer.addPageDictEntry(PdfName.ROTATE, orientation);
	}
}
