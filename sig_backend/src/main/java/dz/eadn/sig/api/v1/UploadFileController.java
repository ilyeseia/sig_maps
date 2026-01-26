package dz.eadn.sig.api.v1;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dz.eadn.sig.constants.Constants;
import dz.eadn.sig.service.UploadFileService;

/**
 * @author Ameur LAMOUR && LOKBANI Chouaib
 *
 */
@RestController
@RequestMapping("/api/v1.0")
public class UploadFileController {

    @Autowired
    private UploadFileService fileService;

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('FILE_UPLOAD_AUTHORITY')")
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestBody MultipartFile uploadfile) {
        return handleUpload(null, uploadfile);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('FILE_UPLOAD_AUTHORITY')")
    @PostMapping("/upload/{folderName}")
    public ResponseEntity<?> uploadFile(@PathVariable String folderName, @RequestBody MultipartFile uploadfile) {
        return handleUpload(sanitizeFolderName(folderName), uploadfile);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('FILE_DELETE_AUTHORITY')")
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileName) {
        return handleDelete(() -> fileService.deleteFile(fileName));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('FILE_DELETE_AUTHORITY')")
    @DeleteMapping("/delete/{folderName}/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String folderName, @PathVariable String fileName) {
        return handleDelete(() -> fileService.deleteFile(sanitizeFolderName(folderName), fileName));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('FILE_DELETE_AUTHORITY')")
    @PutMapping("/delete/{folderName}")
    public ResponseEntity<?> deleteListFiles(@PathVariable String folderName,
            @RequestBody List<String> arrayFilesNames) {
        return handleDelete(
                () -> fileService.deleteFilesListOfDirectory(sanitizeFolderName(folderName), arrayFilesNames));
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> loadFile(@PathVariable String fileName) {
        return handleDownload(Constants.FOLDER_IMAGES_IMAGES, fileName);
    }

    @GetMapping("/download/{folderName}/{fileName}")
    public ResponseEntity<?> loadFile(@PathVariable String folderName, @PathVariable String fileName) {
        return handleDownload(sanitizeFolderName(folderName), fileName);
    }

    // --- Helper Methods ---

    private String sanitizeFolderName(String folderName) {
        // Replaces dots with slashes for nested folders structure (e.g.
        // id_layers.id_feature -> id_layers/id_feature)
        return folderName.replace(".", "/");
    }

    private ResponseEntity<?> handleUpload(String folderName, MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.ok().body("please select a full file!");
        }
        try {
            String fileName = (folderName == null)
                    ? fileService.uploadFile(file)
                    : fileService.uploadFile(folderName, file);
            return ResponseEntity.ok().body(fileName);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private ResponseEntity<?> handleDownload(String folderName, String fileName) {
        try {
            if (fileName == null || fileName.isEmpty()) {
                throw new IOException("File name is empty/invalid");
            }
            InputStream file = fileService.loadFile(folderName, fileName);
            String mimeType = Files.probeContentType(Paths.get(fileName));
            return ResponseEntity.ok()
                    .header("content-type", mimeType)
                    .body(new org.springframework.core.io.InputStreamResource(file));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Please provide the name of file !");
        }
    }

    private ResponseEntity<?> handleDelete(ThrowingRunnable action) {
        try {
            action.run();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws IOException;
    }
}
