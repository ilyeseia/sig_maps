package dz.eadn.sig.repository;

import org.springframework.context.annotation.Primary;

import dz.eadn.sig.model.SettingsType;
import dz.eadn.sig.repository.common.CommonRepository;

/**
 * @author Ameur LAMOUR
 *
 */
@Primary
public interface SettingsTypeRepository extends CommonRepository<SettingsType> {
	SettingsType findByCode(String code);
}
