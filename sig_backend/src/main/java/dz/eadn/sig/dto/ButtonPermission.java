package dz.eadn.sig.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author LOKBANI Chouaib
 *
 */

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ButtonPermission {

    UUID entityElementId;
    String layerSlug;
    String permission;
    Boolean isAllowed = false;


}
