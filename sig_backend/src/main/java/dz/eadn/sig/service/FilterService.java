package dz.eadn.sig.service;

import dz.eadn.sig.dto.FilterDto;
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.dto.ShareFilterDto;
import dz.eadn.sig.dto.UserSimpleDtoWithFilterProjection;
import dz.eadn.sig.model.Filter;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.UserLayerFilter;
import dz.eadn.sig.service.common.CommonService;

import java.util.List;
import java.util.UUID;

public interface FilterService extends CommonService<Filter, FilterDto> {

    FilterDto addFilter(FilterDto filter, String layerSlug);

    FilterDto updateFilter(FilterDto filter);

    List<UserLayerFilter> getFilterByLayer(Layer layer, Boolean distinct);

    void deleteFilter(UUID filterId);

    PageDto<FilterDto> getUserFilters(Integer page, Integer limit, String sort, String dir);

    void shareFilter(ShareFilterDto shareFilterDto);

    PageDto<UserSimpleDtoWithFilterProjection> getUsersBelongsToFilter(UUID filterId, Integer page, Integer limit, String sort,
                                                                       String dir);



}
