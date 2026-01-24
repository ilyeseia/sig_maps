package dz.eadn.sig.dto;

import java.util.UUID;

import dz.eadn.sig.model.Privacy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MapSimpleDto {
	private UUID id;
	private String name;
	private String slug;
	private Privacy privacy;
	private String createdBy;

}
