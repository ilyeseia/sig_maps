package dz.eadn.sig.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.LayerDto;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.service.EntityElementService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour
 *
 */
@Component
@Slf4j
public class ShapeFileReader implements EntityElementReader {
	@Autowired
	private EntityElementService eeService;

	private static final String TEMP_UNZIP_DIR = System.getProperty("java.io.tmpdir") + File.separator
			+ "shape_temp_unzip_dir";

	public ShapeFileReader() {

	}

	public File getShapeFile(String layerName) {
		FilenameFilter shpFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".shp");
			}
		};

		File shapeDir = new File(TEMP_UNZIP_DIR + File.separator + layerName);

		return shapeDir.listFiles(shpFilter)[0];
	}

	@Override
	public Layer readEntityElements(LayerDto layerDto, InputStream is) {
		Layer layer = null;
		ZipInputStream zipInputStream = new ZipInputStream(is);
		ZipEntry entry;
		byte[] buffer = new byte[2048];
		Path outDir = Paths.get(TEMP_UNZIP_DIR + File.separator + layerDto.getName());
		outDir.toFile().mkdirs();
		try {
			while ((entry = zipInputStream.getNextEntry()) != null) {
				Path filePath = outDir.resolve(entry.getName());

				try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
						BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length)) {

					int len;
					while ((len = zipInputStream.read(buffer)) > 0) {
						bos.write(buffer, 0, len);
					}
				}
			}
			File shapeFile = getShapeFile(layerDto.getName());
			layer = readEntityElementsFromShapeFile(layerDto, shapeFile);
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return layer;
	}

	private Layer readEntityElementsFromShapeFile(LayerDto layerDto, File shpFile) throws Exception {
		return eeService.importEntityElementsFromFeatureCollection(layerDto, readFeatureCollectionFromShape(shpFile));
	}

	private SimpleFeatureCollection readFeatureCollectionFromShape(File shpFile) throws IOException {
		SimpleFeatureSource featureSource = null;
		DataStore dataStore = null;

		try {
			if ((!shpFile.exists()) && (!shpFile.isFile())) {
				String message = "SHP file is not found";
				log.error(message);
				throw new FileNotFoundException(message);
			}

			Map<String, Serializable> connect = new HashMap<String, Serializable>();
			connect.put("url", shpFile.toURI().toURL());

			dataStore = DataStoreFinder.getDataStore(connect);
			String[] typeNames = dataStore.getTypeNames();
			String typeName = typeNames[0];

			featureSource = dataStore.getFeatureSource(typeName);

			// writeToPostgisDataBase(featureSource);

		} catch (FileNotFoundException e) {
			log.error(e.getLocalizedMessage());
		} catch (MalformedURLException e) {
			log.error(e.getLocalizedMessage());
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		} finally {
			shpFile = null;
		}

		return featureSource.getFeatures();

	}

	@Override
	public String dataName() {
		return "shp";
	}
}
