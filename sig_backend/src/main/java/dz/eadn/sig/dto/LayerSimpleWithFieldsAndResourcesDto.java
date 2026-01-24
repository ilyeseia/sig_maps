package dz.eadn.sig.dto;

import java.util.ArrayList;
import java.util.List;

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
public class LayerSimpleWithFieldsAndResourcesDto extends LayerSimpleDto {
	private List<FieldSimpleWithResourceDto> fields = new ArrayList<>();
}
