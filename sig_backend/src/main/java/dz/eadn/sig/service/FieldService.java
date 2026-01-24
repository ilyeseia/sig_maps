package dz.eadn.sig.service;

import java.util.Optional;

import dz.eadn.sig.dto.FieldDto;
import dz.eadn.sig.model.Field;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.service.common.CommonService;

/**
 * @author Achrouf Abdenour & Ameur
 *
 */
public interface FieldService extends CommonService<Field, FieldDto> {
	Optional<Field> findByNameAndLayer(String name, Layer layer);

	Optional<Field> findFieldBySlugAndLayer(String slug, Layer layer);

	Field findBySlug(String slug);

	boolean findBySlugAndLayer(String slug, Layer layer);
}
