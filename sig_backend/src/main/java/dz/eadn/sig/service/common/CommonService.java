package dz.eadn.sig.service.common;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import dz.eadn.sig.model.Layer;
import org.locationtech.jts.geom.Geometry;

import dz.eadn.sig.dto.CommonFilter;
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.util.SearchCriteria;
import dz.eadn.sig.util.WITHUUID;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Achrouf Abdenour & Ameur LAMOUR
 *
 */
@Qualifier("commonService")
public interface CommonService<CommonObject extends WITHUUID, CommonDto extends WITHUUID> {
	public CommonObject findById(UUID id, boolean getDeleted);

	public CommonObject findById(UUID id);

	public CommonDto save(CommonDto commonDto);

	public void delete(UUID id);

	public void deleteAll(Collection<CommonObject> instances);

	public List<CommonObject> saveAll(Collection<CommonObject> instances) throws Exception;

	public List<CommonObject> findAll(Integer page, Integer limit, String sort, String dir, boolean getDeleted);

	public List<CommonObject> findAll(Integer page, Integer limit, String sort, String dir);

	public long count();

	public boolean existsById(UUID id);

	public Predicate findBySpatial(SearchCriteria criteria, Root<?> root, Geometry geometry);

	public Predicate findByField(SearchCriteria criteria, Root<?> rootn, Layer layer);

	String buildQuery(String fieldType, SearchCriteria criteria, boolean fullQuery);

	public Predicate findByProperty(SearchCriteria criteria, CriteriaBuilder cb, Root<?> root);

	public boolean isUUID(String string);

	public List<CommonObject> findByAdvancedFilter(CommonFilter filter, Integer page, Integer limit, String sort,
			String dir);

	public PageDto<CommonDto> findAllByFilter(CommonFilter filter, Integer page, Integer limit, String sort,
			String dir);

	public PageDto<CommonDto> findAllByPage(Integer page, Integer limit, String sort, String dir);

}
