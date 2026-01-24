package dz.eadn.sig.dto;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EntityElementSimpleDto {

	private UUID id;
	private Map<String, String> properties = new LinkedHashMap<>();
	private Geometry geom;
	private String geometryType;
	private Coordinate[] coordinates;

}
