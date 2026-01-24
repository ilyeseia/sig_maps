package dz.eadn.sig.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.SettingsTypeDto;
import dz.eadn.sig.model.SettingsType;
import dz.eadn.sig.service.SettingsTypeService;

/**
 * @author Ameur LAMOUR
 *
 */
@Component
public class SettingsTypeMapper extends CommonMapper<SettingsType, SettingsTypeDto> {

	@Autowired
	private SettingsTypeService settingsTypeService;

	@Override
	protected SettingsTypeDto mapEntityToDto(SettingsType entity) {
		SettingsTypeDto settingsTypeDto = new SettingsTypeDto();
		settingsTypeDto.setCode(entity.getCode());
		settingsTypeDto.setDescription(entity.getDescription());
		settingsTypeDto.setDefault_value(entity.getDefault_value());
		settingsTypeDto.setEnabled(entity.getEnabled());

		return settingsTypeDto;
	}

	@Override
	protected SettingsType mapDtoToEntity(SettingsTypeDto dto) {

		SettingsType settingsType = settingsTypeService.findById(dto.getId(), true);

		if (settingsType == null) {
			if (dto.getId() != null) {
				return null;
			}

			settingsType = new SettingsType();
		} else {
			if (settingsType.getDeleted())
				throw new RuntimeException("can't do operation on deleted setting");
		}

		if (dto.getCode() != null) {
			settingsType.setCode(dto.getCode());
		}

		if (dto.getDescription() != null) {
			settingsType.setDescription(dto.getDescription());
		}

		if (dto.getDefault_value() != null) {
			settingsType.setDefault_value(dto.getDefault_value());
		}

		if (dto.getEnabled() != null) {
			settingsType.setEnabled(dto.getEnabled());
		}

		return settingsType;

	}
}
