package dz.eadn.sig.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LAMOUR AMEUR
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GlobalFilterDto {
	private String searchText;
	private List<String> layersSlug;
	private List<String> fieldsSlug;
	private String layerIds;

}
