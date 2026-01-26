package dz.eadn.sig.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Ameur LAMOUR
 *
 */
public interface UploadFileService {
	public String uploadFile(MultipartFile file) throws IOException;

	public String uploadFile(String folderName, MultipartFile file) throws IOException;

	public String uploadByteArray(ByteArrayOutputStream os) throws IOException;

	public java.io.InputStream loadFile(String fileName) throws IOException;

	public java.io.InputStream loadFile(String folderName, String fileName) throws IOException;

	public void deleteFile(String fileName) throws IOException;

	public void deleteFile(String folderName, String fileName) throws IOException;

	public void deleteFilesListOfDirectory(String folderName, List<String> filesNames) throws IOException;

	public void deleteFolder(String path, String folderName) throws IOException;

	public File getFile(String fileName) throws IOException;

}
