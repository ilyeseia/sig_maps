package dz.eadn.sig.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * @author LOKBANI Chouaib
 *
 */

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CloneLayerDto {

    private String outputName;
    private Boolean cloneFilters;
    private Boolean filtersShare;
    private Boolean cloneUsers;
    private Boolean cloneGroups;

}
