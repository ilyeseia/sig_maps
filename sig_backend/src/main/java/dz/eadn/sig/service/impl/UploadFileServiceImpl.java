package dz.eadn.sig.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dz.eadn.sig.constants.Constants;
import dz.eadn.sig.model.Settings;
import dz.eadn.sig.service.SettingsService;
import dz.eadn.sig.service.UploadFileService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ameur LAMOUR
 *
 */
@Service
@Slf4j
public class UploadFileServiceImpl implements UploadFileService {

    private static final String CODE_FOLDER_IMAGES = "SIG_DEFAULT_PATH_FOLDER_IMAGE";

    @Autowired
    private SettingsService settingsService;

    @Override
    public String uploadByteArray(ByteArrayOutputStream os) throws IOException {
        return saveFile(new ByteArrayInputStream(os.toByteArray()), "png", null);
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        return uploadFile(Constants.FOLDER_IMAGES_IMAGES, file);
    }

    @Override
    public String uploadFile(String folderName, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty");
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        return saveFile(file.getInputStream(), extension, folderName);
    }

    @Override
    public byte[] loadFile(String fileName) throws IOException {
        return Files.readAllBytes(resolvePath(null, fileName));
    }

    @Override
    public byte[] loadFile(String folderName, String fileName) throws IOException {
        return Files.readAllBytes(resolvePath(folderName, fileName));
    }

    @Override
    public void deleteFile(String fileName) throws IOException {
        Files.deleteIfExists(resolvePath(null, fileName));
    }

    @Override
    public void deleteFile(String folderName, String fileName) throws IOException {
        Files.deleteIfExists(resolvePath(folderName, fileName));
    }

    @Override
    public void deleteFilesListOfDirectory(String folderName, List<String> filesToKeep) throws IOException {
        if (filesToKeep == null) return;
        
        List<String> existingFiles = getFilesListOfDirectory(folderName);
        existingFiles.removeAll(filesToKeep); // Calculate files to delete

        for (String fileName : existingFiles) {
            deleteFile(folderName, fileName);
        }
    }

    @Override
    public void deleteFolder(String path, String folderName) throws IOException {
        Path targetPath = resolvePath(path, folderName);
        if (Files.exists(targetPath)) {
            // Recursive delete
            Files.walk(targetPath)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }

    @Override
    public File getFile(String fileName) throws IOException {
        return resolvePath(null, fileName).toFile();
    }

    @Override
    public List<String> getFilesListOfDirectory(String folderPath) {
        Path dir = resolvePath(folderPath, null);
        try {
            if (Files.exists(dir) && Files.isDirectory(dir)) {
                return Files.list(dir)
                    .filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());
            }
        } catch (IOException e) {
            log.error("Error listing files in " + folderPath, e);
        }
        return new ArrayList<>();
    }

    // --- Helper Methods ---

    private String saveFile(InputStream inputStream, String extension, String subFolder) throws IOException {
        String fileName = UUID.randomUUID().toString().toLowerCase() + "." + extension;
        Path path = resolvePath(subFolder, fileName);
        
        Files.createDirectories(path.getParent());
        Files.copy(inputStream, path);
        
        return fileName;
    }

    private Path resolvePath(String subFolder, String fileName) {
        Settings settings = settingsService.findByCode(CODE_FOLDER_IMAGES);
        String basePath = (settings != null) ? settings.getValue() : "";
        
        Path path = Paths.get(basePath);
        if (subFolder != null && !subFolder.isEmpty()) {
            path = path.resolve(subFolder);
        }
        return (fileName != null) ? path.resolve(fileName) : path;
    }
}
