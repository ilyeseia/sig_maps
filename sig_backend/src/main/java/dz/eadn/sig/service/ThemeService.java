package dz.eadn.sig.service;


import dz.eadn.sig.dto.ThemeDto;
import dz.eadn.sig.dto.ThemeSimpleDto;
import dz.eadn.sig.model.Theme;
import dz.eadn.sig.service.common.CommonService;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

/**
 * @author Chouaib LOKBANI
 *
 */
public interface ThemeService extends CommonService<Theme, ThemeDto> {

    ThemeDto createTheme(ThemeDto themeDto);

    ThemeDto updateTheme(ThemeDto themeDto);

    ResponseEntity<?> deleteTheme(UUID themeId, UUID mapId);

    Theme getDefaultMapTheme(UUID map);

    void setDefaultMapTheme(UUID themeId, UUID mapId);

    List<ThemeSimpleDto> findAllMapThemes(UUID mapId);
}
