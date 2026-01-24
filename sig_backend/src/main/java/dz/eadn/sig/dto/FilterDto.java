package dz.eadn.sig.dto;

import dz.eadn.sig.util.WITHUUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author LOKBANI Chouaib
 *
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FilterDto extends WITHUUID {

    private String name;

    private String description;

    private String filterConfig;

    private Date createDate;

    private List<String> filterClonedFrom;

}
