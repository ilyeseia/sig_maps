
package dz.eadn.sig.repository;

import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.Map;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

import dz.eadn.sig.model.MapLayer;
import dz.eadn.sig.model.MapLayerId;

import java.util.List;
import java.util.UUID;

/**
 * @author A.LAMOUR
 *
 */
@Primary
public interface MapLayerRepository extends JpaRepository<MapLayer, MapLayerId> {

    List<MapLayer> findAllByLayer(Layer layer);

    List<MapLayer> findAllByMap(Map map);

    MapLayer findByMapLayerId(UUID mapLayerId);

    void deleteByMapLayerId(UUID mapLayerId);

}
