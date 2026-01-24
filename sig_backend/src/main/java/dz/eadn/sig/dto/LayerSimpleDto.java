package dz.eadn.sig.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import dz.eadn.sig.model.LayerType;
import dz.eadn.sig.model.Style;
import dz.eadn.sig.model.SymbologyType;
import dz.eadn.sig.model.TypeLimit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author A.LAMOUR
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LayerSimpleDto {
	private UUID id;
	private UUID mapLayerId;
	private LayerType type;
	private String name;
	private String slug;
	private String topo;
	private Boolean visible = true;
	private String identifiant;
	private int order;
	private TypeLimit typeLimit;
	private Date createDate;
	private String createdBy;
	private LayerStyleSimpleDto style;

}
