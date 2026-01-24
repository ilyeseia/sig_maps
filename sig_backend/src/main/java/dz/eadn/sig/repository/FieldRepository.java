package dz.eadn.sig.repository;

import java.util.Optional;

import org.springframework.context.annotation.Primary;

import dz.eadn.sig.model.Field;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.repository.common.CommonRepository;

/**
 * @author Achrouf Abdenour
 *
 */
@Primary
public interface FieldRepository extends CommonRepository<Field> {
	Optional<Field> findByNameAndLayer(String name, Layer layer);
	Optional<Field> findBySlugAndLayer(String slug, Layer layer);
	Optional<Field> findBySlug(String slug);
}
