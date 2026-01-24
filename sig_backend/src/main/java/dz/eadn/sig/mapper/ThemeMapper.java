package dz.eadn.sig.mapper;


import dz.eadn.sig.dto.GroupDto;
import dz.eadn.sig.dto.ThemeDto;
import dz.eadn.sig.model.Group;
import dz.eadn.sig.model.Style;
import dz.eadn.sig.model.Theme;
import dz.eadn.sig.service.ThemeService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @author Chouaib LOKBANI
 *
 */

@Component
@Primary
public class ThemeMapper extends CommonMapper<Theme, ThemeDto> {

    @Autowired
    private ThemeService themeService;

    @Autowired
    private MapMapper mapMapper;

    @Override
    protected ThemeDto mapEntityToDto(Theme entity) {

        ThemeDto themeDto = new ThemeDto();
        themeDto.setName(entity.getName());

        if(entity.getMap() != null){
            themeDto.setMap(mapMapper.mapEntityToDto(entity.getMap()));
        }

        return themeDto;
    }

    @Override
    protected Theme mapDtoToEntity(ThemeDto dto) {
        Theme theme = themeService.findById(dto.getId(), true);
        if (theme == null) {
            if (dto.getId() != null) {
                return null;
            }

            theme = new Theme();
        } else {
            if (theme.getDeleted())
                throw new RuntimeException("can't do operation on deleted layerStyle");
        }
        theme.setName(dto.getName());
        theme.setIsDefault(theme.getIsDefault());
        if(dto.getMap() != null){
            theme.setMap(mapMapper.mapDtoToEntity(dto.getMap()));
        }
        return theme;
    }
}























