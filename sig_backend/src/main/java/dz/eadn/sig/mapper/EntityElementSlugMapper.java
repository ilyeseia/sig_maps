package dz.eadn.sig.mapper;

import java.io.IOException;
import java.util.List;

import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.EntityElementDto;
import dz.eadn.sig.model.EntityElement;
import dz.eadn.sig.service.EntityElementService;
import dz.eadn.sig.util.Utils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Achrouf Abdenour
 *
 */
@Component
@Slf4j
public class EntityElementSlugMapper extends EntityElementMapper {

	@Autowired
	private Utils utilBean;

	@Autowired
	private EntityElementService eeService;

	@Autowired
	private TagMapper tagMapper;

	@Override
	protected EntityElementDto mapEntityToDto(EntityElement entity) {
		EntityElementDto entityElementDto = new EntityElementDto();
		entityElementDto.setId(entity.getId());

		entityElementDto.setFeatureJson(utilBean.writeEntityElementToJson(entity, true, true));
		entityElementDto.setLayerSlug(entity.getLayer().getSlug());
		entityElementDto.setLayerIdentifiant(entity.getLayer().getIdentifiant());

		return entityElementDto;
	}

	@Override
	public EntityElement dtoToEntity(EntityElementDto dto) {
		EntityElement ee = eeService.findById(dto.getId(), true);
		if (ee == null) {
			if (dto.getId() != null)
				return null;
		} else {
			if (ee.getDeleted())
				throw new RuntimeException("can't do operaton on deleted entity element");
		}

		if (dto.getFeatureJson() != null) {
			FeatureJSON featureJson = new FeatureJSON();
			SimpleFeature sm = null;
			try {
				sm = (SimpleFeature) featureJson.readFeature(dto.getFeatureJson());
			} catch (IOException e) {
				log.error(e.getMessage());
			}
			ee = eeService.featureToEntityElement(dto.getLayerSlug(), sm, true);

			if (dto.getId() != null)
				ee.setId(dto.getId());
		}

		if (dto.getTagDtos() != null)
			ee.setTags(tagMapper.dtosToEntitys(dto.getTagDtos()));

		if (dto.getLayerIdentifiant() != null) {
			ee.getLayer().setIdentifiant(dto.getLayerIdentifiant());
		}

		return ee;
	}

}
