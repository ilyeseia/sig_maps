package dz.eadn.sig.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.SettingsDto;
import dz.eadn.sig.model.Settings;
import dz.eadn.sig.model.SettingsType;
import dz.eadn.sig.service.SettingsService;
import dz.eadn.sig.service.SettingsTypeService;

/**
 * @author Achrouf Abdenour && Ameur LAMOUR
 *
 */
@Component
public class SettingsMapper extends CommonMapper<Settings, SettingsDto> {

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private SettingsTypeService settingsTypeService;

	@Override
	protected SettingsDto mapEntityToDto(Settings entity) {
		SettingsDto settingsDto = new SettingsDto();
		settingsDto.setCode(entity.getCode());
		settingsDto.setType(entity.getType().getCode());
		settingsDto.setValue(entity.getValue());
		settingsDto.setDefault_value(entity.getDefault_value());
		settingsDto.setEnabled(entity.getEnabled());

		return settingsDto;
	}

	@Override
	protected Settings mapDtoToEntity(SettingsDto dto) {

		Settings settings = settingsService.findById(dto.getId(), true);

		if (settings == null) {
			if (dto.getId() != null) {
				return null;
			}

			settings = new Settings();
		} else {
			if (settings.getDeleted())
				throw new RuntimeException("can't do operation on deleted setting");
		}

		if (dto.getCode() != null) {
			settings.setCode(dto.getCode());
		}

		if (dto.getValue() != null) {
			settings.setValue(dto.getValue());
		}

		if (dto.getType() != null) {

			SettingsType st = settingsTypeService.findByCode(dto.getType());
			if (st != null)
				settings.setType(st);
		}

		if (dto.getDefault_value() != null) {
			settings.setDefault_value(dto.getDefault_value());
		}

		if (dto.getEnabled() != null) {
			settings.setEnabled(dto.getEnabled());
		}

		return settings;

	}
}
