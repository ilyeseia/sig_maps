package dz.eadn.sig.repository;

import java.util.List;

import org.springframework.context.annotation.Primary;

import dz.eadn.sig.model.Settings;
import dz.eadn.sig.model.SettingsType;
import dz.eadn.sig.repository.common.CommonRepository;

/**
 * @author Achrouf Abdenour
 *
 */
@Primary
public interface SettingsRepository extends CommonRepository<Settings> {
	Settings findByCode(String code);

	List<Settings> findByTypeAndEnabled(SettingsType type, boolean enabled);

}
