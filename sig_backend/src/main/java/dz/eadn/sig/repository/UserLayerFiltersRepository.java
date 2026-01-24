package dz.eadn.sig.repository;

import dz.eadn.sig.model.*;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * @author LOKBANI Chouaib
 *
 */
@Primary
public interface UserLayerFiltersRepository extends JpaRepository<UserLayerFilter, UserLayerFilterId> {

    List<UserLayerFilter> findUserLayerFilterByUser(User user);

    List<UserLayerFilter> findUserLayerFilterByLayer(Layer layer);

    List<UserLayerFilter> findDistinctByLayer(Layer layer);

    List<UserLayerFilter> findUserLayerFilterByFilterIdAndUser(UUID filterId, User user);

    List<UserLayerFilter> findUserLayerFilterByFilterIdAndLayerId(UUID filterId, UUID layerId);

    int countUserLayerFilterByFilterClonedFromAndUser(UUID filterId, User user);

    void deleteByFilterIdAndFilterClonedFromIsNull(UUID filterId);

    void deleteUserLayerFilterByFilterIdAndUserId(UUID filterId, UUID userId);

}
