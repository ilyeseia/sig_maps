package dz.eadn.sig.repository.common;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import dz.eadn.sig.util.WITHUUID;

/**
 * @author Achrouf Abdenour
 *
 * @param <CommonObject>
 */
@NoRepositoryBean
public interface CommonRepository<CommonObject extends WITHUUID>
		extends JpaSpecificationExecutor<CommonObject>, JpaRepository<CommonObject, UUID> {

}
