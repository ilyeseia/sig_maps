package dz.eadn.sig.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Query;

import dz.eadn.sig.dto.LayerProjection;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.TypeLimit;
import dz.eadn.sig.repository.common.CommonRepository;

/**
 * @author Achrouf Abdenour
 *
 */
@Primary
public interface LayerRepository extends CommonRepository<Layer> {

	Layer findBySlug(String slug);

	Layer findByNameIgnoreCase(String name);


	@Query(value = "select l.name, l.slug from sig.layer l", nativeQuery = true)
	List<LayerProjection> findAllLayer();

	@Query(value = "select count(ml.layers_id)  from sig.map_users mu inner join sig.map_layers ml on ml.maps_id = mu.maps_id  where  ml.maps_id = (select m.id from sig.map m where m.slug = ?2)  and mu.users_id = ?1 and ml.layers_id = ?3", nativeQuery = true)
	int checkIfLayerBelongsToUserMap(UUID userId, String mapSlug, UUID layerId);

	@Query(value = "select count(r1.groups_id) from \n"
			+ " (select * from sig.group_users gu where gu.users_id = ?1) as r1\n"
			+ "where  r1.groups_id IN (select mg.groups_id from  sig.map_groups mg inner join sig.map_layers ml on ml.maps_id = mg.maps_id  where   ml.maps_id = (select m.id from sig.map m where m.slug = ?2) and ml.layers_id = ?3)", nativeQuery = true)
	int checkIfLayerBelongsToUserGroupsMap(UUID userId, String mapSlug, UUID layerId);


	@Query(value= "select count(r1.groups_id) from \n" +
			"\t\t\t(select * from sig.group_users gu where gu.users_id = ?1) as r1\n" +
			"\t\t\twhere  r1.groups_id IN (select lg.groups_id from  sig.layer_groups lg  where lg.layers_id = ?2)", nativeQuery = true)
	int checkIfLayerSharedWithUserGroups(UUID userId, UUID layerId);

	List<Layer> findByTypeLimit(TypeLimit typeLimit);

	List<Layer> findAllByIdIn(List<UUID> layersIds);

	List<LayerProjection> findAllByIdInOrderByName(List<UUID> layersIds);

	LayerProjection findDistinctById(UUID layerId);

}
