package dz.eadn.sig.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LAMOUR AMEUR
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BufferParamsDto {
	private List<String> center;
	private float radius;

}
