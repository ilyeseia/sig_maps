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
public class LayerSimpleWithFieldsDto extends LayerSimpleDto {

	private List<FieldSimpleDto> fields = new ArrayList<>();

}
