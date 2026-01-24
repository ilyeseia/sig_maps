package dz.eadn.sig.api.v1;

import dz.eadn.sig.model.Settings;
import dz.eadn.sig.service.SettingsService;
import dz.eadn.sig.util.Utils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Achrouf Abdenour && Ameur LAMOUR
 *
 */
@RestController
@RequestMapping("/api/v1.0/files")
public class FileController {

    private static final String CODE_FOLDER_IMAGES = "SIG_DEFAULT_PATH_FOLDER_IMAGE";

    private Utils utils;

    private SettingsService settingsService;

    public FileController(Utils utils, SettingsService settingsService) {
        this.utils = utils;
        this.settingsService = settingsService;
    }

    @Operation(summary = "Find all files in folder", description = "find all files in directory with parameter folder_name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieve files", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")})
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('CONFIGURE_LAYER_STYLE_AUTHORITY')")
    @GetMapping("/{folder}")
    public List<Map<String, String>> findAllFilesOfFolder(@PathVariable("folder") String nameFolder) {
        try {
            Settings settings = settingsService.findByCode(CODE_FOLDER_IMAGES);
            List<Map<String, String>> icons = new ArrayList<>();
            if (settings != null){
                nameFolder = nameFolder.replace(".", "/");
                String path = settingsService.findByCode(CODE_FOLDER_IMAGES).getValue()  +  nameFolder;
                File f = new File(path);
                for (File file : f.listFiles()) {
                    Map<String, String> data = new HashMap<>();
                    data.put("name", file.getName());
                    data.put("path", utils.getFileUrl(nameFolder.replace("/", ".") + "/", file.getName()));
                    icons.add(data);
                }
            }
            return icons;
        } catch (Exception e) {
            return null;
        }

    }
}
