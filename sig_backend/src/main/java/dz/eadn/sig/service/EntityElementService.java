package dz.eadn.sig.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import dz.eadn.sig.model.CLASSIFICATIONMODE;
import dz.eadn.sig.model.PostgisOperation;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;

import dz.eadn.sig.dto.CommonFilter;
import dz.eadn.sig.dto.EntityElementDto;
import dz.eadn.sig.dto.EntityElementSimpleDto;
import dz.eadn.sig.dto.GlobalFilterDto;
import dz.eadn.sig.dto.LayerDto;
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.model.EntityElement;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.service.common.CommonService;
import dz.eadn.sig.util.EntityElementReader;
import dz.eadn.sig.util.EntityElementWriter;
import dz.eadn.sig.util.SearchCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

/**
 * @author Achrouf Abdenour & Lamour Ameur & LOKBANI Chouaib
 *
 */
public interface EntityElementService extends CommonService<EntityElement, EntityElementDto> {
	public EntityElementReader getReader(String dataName);

	public EntityElementWriter getWriter(String dataName);

	public Predicate findBySpatial(SearchCriteria criteria, Root<?> root, Geometry geometry);

	public Predicate findByProperty(SearchCriteria criteria, CriteriaBuilder cb, Root<?> root);

	public PageDto<EntityElementDto> findByAdvancedFilter(CommonFilter filter, String layerSlug, Integer page,
			Integer limit, String sort, String dir);

	public PageDto<EntityElementDto> findNearestEntityElements(UUID selectedEntityElement,
													 UUID targetLayer,
													 String targetLayerSlug,
													 String mapSlug,
													 double perimeter,
													 String unit,
													 String geometry,
													 PostgisOperation operation,
													 boolean intersection,
												     PostgisOperation algebraOperation,
													 @PageableDefault(size=10, page=0) Pageable pageable);

	public List<String> findAllByProperty(String fieldName, UUID layerId);

	public List<String> groupByProperty(String fieldName, UUID layerId);

	public List<Map<String, Object>> getEntityElementForReporting(String layerSlug);

	public List<Map<String, Object>> getEntityElementForSecurityRestrictions(UUID layerId, String identifiant);

	public List<Map<String, Object>> findAllByProperty(String fieldName, UUID layerId,
			CLASSIFICATIONMODE classficationMode, int classes);

	public Layer importEntityElementsFromFeatureCollection(LayerDto layerDto, SimpleFeatureCollection fc)
			throws Exception;

	public EntityElement featureToEntityElement(Layer layer, SimpleFeature feature, boolean withSlug);

	public EntityElement featureToEntityElement(String layerSlug, SimpleFeature feature, boolean withSlug);

	public List<Map<String, Object>> findPropertyByLayer(String fieldName, String layerSlug);

	public String getConvexHull(String multipoint);

	public String getBuffer(List<String> center, float radius);

	public String getCentroId(UUID id);

	public boolean isAuthorizedArea(Geometry geometry);

	public PageDto<EntityElement> findAllBySearch(GlobalFilterDto globalFilterDto, Integer page, Integer limit,
			String sort, String dir);

	public PageDto<EntityElementSimpleDto> findAllBySearchInViewTable(String search, String layerSlug, String mapSlug,  Integer page,
			Integer limit, String sort, String dir);

	public PageDto<EntityElementSimpleDto> findAllByLayer(String layerSlug, Integer page, Integer limit, String sort,
			String dir);
}
