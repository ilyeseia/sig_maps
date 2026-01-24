package dz.eadn.sig.repository;

import org.springframework.context.annotation.Primary;

import dz.eadn.sig.model.Tag;
import dz.eadn.sig.repository.common.CommonRepository;
import dz.eadn.sig.repository.custom.CustomTagRepository;

/**
 * @author Achrouf Abdenour
 *
 */
@Primary
public interface TagRepository extends CommonRepository<Tag>, CustomTagRepository {
	public Tag findByName(String name);
}
