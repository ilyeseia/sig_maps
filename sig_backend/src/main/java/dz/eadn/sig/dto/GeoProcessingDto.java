package dz.eadn.sig.dto;


import dz.eadn.sig.util.SearchCriteria;
import dz.eadn.sig.util.WITHUUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

/**
 * @author LOKBANI Chouaib
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GeoProcessingDto extends WITHUUID {

    String operation;

    String layerIdA;

    CommonFilter layerFilterA;

    String layerIdB;

    CommonFilter layerFilterB;

    String newLayerName;

    String workingMap;

    ThemeDto targetTheme;

    String outputFormat;

    String ext;

    List<FieldDto> selectedFields;

    HashMap<String, String> buffer;

    LayerStyleDto layerStyle;

    int order;

}
