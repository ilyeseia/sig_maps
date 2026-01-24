package dz.eadn.sig.repository;

import dz.eadn.sig.model.Style;
import dz.eadn.sig.repository.common.CommonRepository;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.UUID;

/**
 * @author Chouaib LOKBAN
 *
 */
@Primary
public interface LayerStylesRepository extends CommonRepository<Style> {

    List<Style> findAllByMapLayer_MapLayerIdInAndIsDefaultTrue(List<UUID> mapLayersId);

    List<Style> findAllByMapLayer_MapLayerId(UUID mapLayersId);

    int countAllByName(String name);

}
