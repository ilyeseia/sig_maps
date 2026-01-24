package dz.eadn.sig.service;

import java.util.List;

import dz.eadn.sig.dto.CustomJoinFilter;
import dz.eadn.sig.dto.TagDto;
import dz.eadn.sig.model.Tag;
import dz.eadn.sig.service.common.CommonService;

/**
 * @author Achrouf Abdenour & Ameur
 *
 */
public interface TagService extends CommonService<Tag, TagDto> {
	public Tag findByName(String name);

	public List<Object> findAllByType(CustomJoinFilter customJoinFilter);
}
