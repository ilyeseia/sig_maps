package dz.eadn.sig.repository;

import dz.eadn.sig.model.Theme;
import dz.eadn.sig.repository.common.CommonRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Example;

import java.util.List;
import java.util.UUID;


/**
 * @author Chouaib LOKBANI
 *
 */
@Primary
public interface ThemeRepository extends CommonRepository<Theme> {

    Theme findByMap_IdAndIsDefaultTrue(UUID mapId);

    List<Theme> findAllByMap_Id(UUID mapId);

    int countByMap_IdAndName(UUID map, String name);

}
