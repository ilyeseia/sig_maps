package dz.eadn.sig.mapper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import dz.eadn.sig.dto.LayerStyleDto;
import dz.eadn.sig.model.Settings;
import dz.eadn.sig.model.Style;
import dz.eadn.sig.service.LayerStylesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @author Chouaib LOKBANI
 *
 */

@Component
@Primary
public class LayerStylesMapper extends CommonMapper<Style, LayerStyleDto> {

    @Autowired
    private LayerStylesService layerStylesService;


    @Override
    protected LayerStyleDto mapEntityToDto(Style entity) {
        LayerStyleDto layerStyleDto = new LayerStyleDto();
        layerStyleDto.setName(entity.getName());
        layerStyleDto.setDisplayName(entity.getDisplayName());
        layerStyleDto.setIsDefault(entity.getIsDefault());
        layerStyleDto.setStyle(entity.getStyle());
        layerStyleDto.getStyleConfig().setSymbologyType(entity.getSymbologyType());
        return layerStyleDto;
    }

    @Override
    protected Style mapDtoToEntity(LayerStyleDto dto) {
        Style layerStyle = layerStylesService.findById(dto.getId(), true);
        if (layerStyle == null) {
            if (dto.getId() != null) {
                return null;
            }

            layerStyle = new Style();
        } else {
            if (layerStyle.getDeleted())
                throw new RuntimeException("can't do operation on deleted layerStyle");
        }
        layerStyle.setIsDefault(dto.getIsDefault());
        layerStyle.setDisplayName(dto.getDisplayName());

        //Theme object already mapped
        if(dto.getTheme() != null){
            layerStyle.setTheme(dto.getTheme());
        }

        if(dto.getStyleConfig() != null) {
            layerStyle.setSymbologyType(dto.getStyleConfig().getSymbologyType());
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            try {
                layerStyle.setStyle(ow.writeValueAsString(dto.getStyleConfig()));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return layerStyle;
    }
}