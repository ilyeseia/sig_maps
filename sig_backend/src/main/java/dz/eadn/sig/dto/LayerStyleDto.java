package dz.eadn.sig.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dz.eadn.sig.model.SymbologyType;
import dz.eadn.sig.model.Theme;
import dz.eadn.sig.util.WITHUUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * @author Chouaib LOKBANI
 *
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LayerStyleDto extends WITHUUID {

    private String name = "default";

    private String displayName = "Style par default";

    private String style;

    private Boolean isDefault = false;

    private StyleDto styleConfig = new StyleDto();

    private LayerSimpleDto layer;

    private UUID mapId;

    private UUID mapLayer;

    @JsonIgnore
    private SymbologyType symbologyType;

    @JsonIgnore
    private Boolean labelingEnabled;

    @JsonIgnore
    private String iconUrl;

    @JsonIgnore
    private Boolean customIcon;

    @JsonIgnore
    private Theme theme;

}
