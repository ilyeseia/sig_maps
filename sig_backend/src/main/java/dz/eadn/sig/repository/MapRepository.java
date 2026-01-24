package dz.eadn.sig.repository;

import dz.eadn.sig.model.Layer;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import dz.eadn.sig.model.Map;
import dz.eadn.sig.model.Privacy;
import dz.eadn.sig.repository.common.CommonRepository;

import java.util.List;
import java.util.UUID;

/**
 * @author Achrouf Abdenour 
 *
 */

/**
 * @author Ameur LAMOUR
 *
 */
@Primary
public interface MapRepository extends CommonRepository<Map> {

	Page<Map> findByPrivacy(Privacy privacy, Pageable pageable);

	Page<Map> findByPrivacyAndIgnoreCaseName(Privacy privacy, String name, Pageable pageable);

	Map findByNameIgnoreCase(String name);

	Map findBySlug(String slug);

	Long countMapByPrivacy(Privacy privacy);

}
