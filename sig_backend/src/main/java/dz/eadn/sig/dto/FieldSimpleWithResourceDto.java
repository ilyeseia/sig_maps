package dz.eadn.sig.dto;

import java.util.UUID;

import dz.eadn.sig.model.FieldType;
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
public class FieldSimpleWithResourceDto {

	private UUID id;
	private String name;
	private FieldType type;
	private String slug;
	private Boolean required;
	private Boolean visible;
	private Boolean publique;
	private UUID parent;
	private int order;
	private ResourceSimpleDto resource;

}
