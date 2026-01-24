package dz.eadn.sig.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ameur LAMOUR
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {

	private String label;
	private String field;
	private String operator;
	private String type;
	private String value;

}
