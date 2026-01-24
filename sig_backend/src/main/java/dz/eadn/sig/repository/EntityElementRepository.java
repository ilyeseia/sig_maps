package dz.eadn.sig.repository;

import dz.eadn.sig.dto.EntityElementDto;
import org.springframework.context.annotation.Primary;

import dz.eadn.sig.model.EntityElement;
import dz.eadn.sig.repository.common.CommonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Achrouf Abdenour, LOKBANI Chouaib
 *
 */
@Primary
public interface EntityElementRepository extends CommonRepository<EntityElement> {


    @Query(value = "select l.name, count(*) total from sig.entity_element e join sig.layer l on l.id = e.layer_entity_element group by l.id order by total desc", nativeQuery = true)
    List<Map<String, ?>> countEntityElementByLayer(Pageable pageable);


    @Query(value = "SELECT * FROM sig.entity_element e \n" +
            "\t where ST_DWithin(e.geom, cast(?1 as geometry), ?2 , true) and \n" +
            "\t e.layer_entity_element = ?3 and e.id <> ?4 and e.deleted = false", nativeQuery = true)
    Page<EntityElement> findEntityElementByDwithin(String geom, double perimeter, UUID selectedLayer, UUID exceptedPoint, Pageable pageable);


    @Query(value = "SELECT * FROM sig.entity_element e where e.layer_entity_element = ?3 and e.id <> ?4 and e.deleted = false and e.id not in (SELECT e.id FROM sig.entity_element e where ST_DWithin(e.geom, cast(?1 as geometry), ?2 , true) \n" +
            "and e.layer_entity_element = ?3  and e.id <> ?4 and e.deleted = false)", nativeQuery = true)
    Page<EntityElement> findEntityElementByBeyond(String geom, double perimeter, UUID selectedLayer, UUID exceptedPoint, Pageable pageable);

    @Query(value = "select * from sig.entity_element e where e.layer_entity_element = ?2  and ST_Intersects(?1, e.geom) and e.deleted = false", nativeQuery = true)
    Page<EntityElement> findEntityElementByIntersection(String geom, UUID selectedLayer,  Pageable pageable);

    @Query(value = "select * from sig.entity_element e where e.layer_entity_element = ?2  and ST_Covers(?1, e.geom) and e.deleted = false ", nativeQuery = true)
    Page<EntityElement> findEntityElementByCover(String geom, UUID selectedLayer,  Pageable pageable);

}