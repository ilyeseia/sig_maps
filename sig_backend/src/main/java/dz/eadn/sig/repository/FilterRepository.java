package dz.eadn.sig.repository;

import dz.eadn.sig.dto.UserSimpleDtoWithFilterProjection;
import dz.eadn.sig.model.Filter;
import dz.eadn.sig.model.UserLayerFilter;
import dz.eadn.sig.repository.common.CommonRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;


/**
 * @author LOKBANI Chouaib
 *
 */
@Primary
public interface FilterRepository extends CommonRepository<Filter> {

    List<Filter> findFilterByUserLayerFiltersIn(List<UserLayerFilter> userLayerFilters);

    Page<Filter> findFilterByUserLayerFiltersIn(Pageable pageable, List<UserLayerFilter> userLayerFilters);

    @Query(value = "select cast(u.id as varchar),  u.user_name as username, u.first_name as firstName, u.last_name as lastName, u.avatar, u.email, cast(filters.filter_cloned_from as varchar) as filterClonedFrom  from sig.user u inner join (select * from sig.user_layer_filter where filter_id = ?1  union select * from sig.user_layer_filter where filter_cloned_from = ?1 and filter_cloned_from != filter_id) as filters on filters.user_id = u.id order by u.first_name", nativeQuery = true)
    Page<UserSimpleDtoWithFilterProjection> getUsersBelongsToUser(UUID filterId, Pageable pageable);

    @Query(value = "select cast(u.id as varchar),  u.user_name as username, u.first_name as firstName, u.last_name as lastName, u.avatar, u.email, cast(filters.filter_cloned_from as varchar) as filterClonedFrom  from sig.user u inner join (select * from sig.user_layer_filter where filter_id = ?1  union select * from sig.user_layer_filter where filter_cloned_from = ?1 and filter_cloned_from != filter_id) as filters on filters.user_id = u.id order by u.first_name", nativeQuery = true)
    List<UserSimpleDtoWithFilterProjection> getUsersBelongsToUserList(UUID filterId);

}
