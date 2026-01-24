package dz.eadn.sig.service;

import java.util.List;

import dz.eadn.sig.dto.SettingsDto;
import dz.eadn.sig.model.Settings;
import dz.eadn.sig.service.common.CommonService;

/**
 * @author Achrouf Abdenour
 *
 */
public interface SettingsService extends CommonService<Settings, SettingsDto> {

	Settings findByCode(String code);

	List<Settings> findByTypeAndEnabled(String type, boolean enabled);

	List<Settings> getStatisticsSetting();
}
