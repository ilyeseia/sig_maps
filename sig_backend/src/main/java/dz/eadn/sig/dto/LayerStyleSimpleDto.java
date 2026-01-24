package dz.eadn.sig.dto;




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
public class LayerStyleSimpleDto extends WITHUUID {

    private String name = "default";

    private String displayName = "Style par default";

    private String style;

    private Boolean isDefault = false;

    private UUID mapLayer;

}
