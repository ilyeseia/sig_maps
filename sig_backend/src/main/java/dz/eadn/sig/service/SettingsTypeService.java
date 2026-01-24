package dz.eadn.sig.service;

import dz.eadn.sig.dto.SettingsTypeDto;
import dz.eadn.sig.model.SettingsType;
import dz.eadn.sig.service.common.CommonService;

public interface SettingsTypeService extends CommonService<SettingsType, SettingsTypeDto> {
	SettingsType findByCode(String code);
}
