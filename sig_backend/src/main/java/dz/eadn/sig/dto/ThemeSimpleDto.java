package dz.eadn.sig.dto;


import dz.eadn.sig.model.Map;
import dz.eadn.sig.model.Style;
import dz.eadn.sig.util.WITHUUID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.List;

/**
 * @author LOKBANI Chouaib
 *
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThemeSimpleDto extends WITHUUID {

    String name;

    Boolean isDefault = false;


}
