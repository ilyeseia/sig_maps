package dz.eadn.sig.dto;

import java.util.UUID;

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
public class DivisionDto {

	private UUID value;
	private String text;
	private String layerSlug;

}
