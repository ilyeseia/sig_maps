package dz.eadn.sig.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.SchemaException;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.model.EntityElement;
import dz.eadn.sig.model.Layer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour
 *
 */
@Component
@Slf4j
public class ShapeFileWriter implements EntityElementWriter {
	@Autowired
	private Utils utils;

	private static final String TEMP_ZIP_DIR = System.getProperty("java.io.tmpdir") + File.separator
			+ "shape_temp_zip_dir";

	public ShapeFileWriter() {

	}

	private File getShpFile(Layer layer) {
		return new File(TEMP_ZIP_DIR + File.separator + layer.getName() + ".shp");
	}

	private File writeEntityElements(Layer layer, List<EntityElement> entityElements)
			throws IOException, SchemaException {
		SimpleFeatureCollection fc = utils.getSimpleFeatureCollectionFromEntityElements(layer, entityElements, false,
				false);
		return writeFeatureCollection(layer, fc);
	}

	private File writeFeatureCollection(Layer layer, SimpleFeatureCollection collection)
			throws IOException, SchemaException {
		File shapeFile = getShpFile(layer);
		shapeFile.getParentFile().mkdirs();
		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

		Map<String, Serializable> params = new HashMap<String, Serializable>();
		params.put("url", shapeFile.toURI().toURL());
		params.put("create spatial index", Boolean.TRUE);

		ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
		SimpleFeatureType schema = utils.createSchemaFromLayer(layer, false, false);
		newDataStore.createSchema(schema);
		newDataStore.forceSchemaCRS(DefaultGeographicCRS.WGS84);

		Transaction transaction = new DefaultTransaction("create");

		String typeName = newDataStore.getTypeNames()[0];
		SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);

		if (featureSource instanceof SimpleFeatureStore) {
			SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

			featureStore.setTransaction(transaction);
			try {
				featureStore.addFeatures(collection);
				transaction.commit();

			} catch (Exception problem) {
				problem.printStackTrace();
				transaction.rollback();

			} finally {
				transaction.close();
			}
			log.info("shape created with success");
		} else {
			log.error(typeName + " does not support read/write access");
		}
		return shapeFile;
	}

	@Override
	public void writeEntityElements(Layer layer, List<EntityElement> entityElements, OutputStream os) {
		File shpFile = null;
		try {
			shpFile = writeEntityElements(layer, entityElements);
		} catch (IOException | SchemaException e) {
			log.error(e.getMessage());
		}
		ZipOutputStream zos = new ZipOutputStream(os);

		final String fileName = shpFile.getName().substring(0, shpFile.getName().lastIndexOf("."));
		File[] shpFiles = new File(shpFile.getParent()).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.contains(fileName);
			}
		});

		for (File file : shpFiles) {
			try {
				zos.putNextEntry(new ZipEntry(FilenameUtils.getName(file.getPath())));
				IOUtils.copy(new FileInputStream(file), zos);
				file.delete();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		try {
			zos.close();
			os.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public String dataName() {
		return "shp";
	}

	@Override
	public String mimeType() {
		return "application/zip";
	}

	@Override
	public String extension() {
		return ".zip";
	}
}
