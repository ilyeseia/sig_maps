package dz.eadn.sig.dto;


import dz.eadn.sig.model.Privacy;
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
public class CloneMapDto {

    private String outputName;
    private Boolean cloneUsers;
    private Boolean cloneGroups;
    private Privacy privacy;

}
