package dz.eadn.sig.mapper;

import dz.eadn.sig.dto.FilterDto;
import dz.eadn.sig.dto.GroupDto;
import dz.eadn.sig.dto.LayerDto;
import dz.eadn.sig.model.*;
import dz.eadn.sig.service.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class FilterMapper extends CommonMapper<Filter, FilterDto> {

    @Autowired
    private FilterService filterService;

    @Override
    protected FilterDto mapEntityToDto(Filter entity) {
        FilterDto filterDto = new FilterDto();
        filterDto.setName(entity.getName());
        filterDto.setFilterConfig(entity.getFilterConfig());
        filterDto.setDescription(entity.getDescription());
        return filterDto;
    }

    public FilterDto mapEntityToDto(Filter entity, UUID userId) {
        FilterDto filterDto = new FilterDto();
        filterDto.setId(entity.getId());
        filterDto.setName(entity.getName());
        filterDto.setFilterConfig(entity.getFilterConfig());
        filterDto.setCreateDate(entity.getCreateDate());
        filterDto.setCreatedBy(entity.getCreatedBy());
        filterDto.setDescription(entity.getDescription());
        filterDto.setFilterClonedFrom(entity.getUserLayerFilters().stream().filter(u -> u.getUser().getId().equals(userId)).map(f -> f.getFilterClonedFrom() != null ? f.getFilterClonedFrom().toString() : null
        ).collect(Collectors.toList()));
        return filterDto;
    }



    public List<FilterDto> entitysToDtos(List<Filter> entitys, UUID userId) {
        return entitys.stream().map(dto -> mapEntityToDto(dto, userId)).filter(dto -> dto != null).collect(Collectors.toList());
    }


    @Override
    protected Filter mapDtoToEntity(FilterDto filterDto) {
        Filter filter = filterService.findById(filterDto.getId(), true);

        if (filter == null) {
            if (filterDto.getId() != null) {
                return null;
            }

            filter = new Filter();
        }

        if (filterDto.getName() != null) {
            filter.setName(filterDto.getName());
        }

        if(filterDto.getDescription() != null){
            filter.setDescription(filterDto.getDescription());
        }

        if(filterDto.getFilterConfig() != null){
            filter.setFilterConfig(filterDto.getFilterConfig());
        }

        return filter;
    }
}
