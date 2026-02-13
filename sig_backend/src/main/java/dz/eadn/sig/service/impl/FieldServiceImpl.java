package dz.eadn.sig.service.impl;

import java.util.Optional;

import dz.eadn.sig.util.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dz.eadn.sig.dto.FieldDto;
import dz.eadn.sig.exceptions.EntityAlreadyExistsException;
import dz.eadn.sig.mapper.FieldMapper;
import dz.eadn.sig.model.Field;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.repository.FieldRepository;
import dz.eadn.sig.service.FieldService;
import dz.eadn.sig.service.common.impl.CommonServiceImpl;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * @author Achrouf Abdenour
 *
 */
@Service
public class FieldServiceImpl extends CommonServiceImpl<Field, FieldDto> implements FieldService {

	@Autowired
	private FieldRepository fieldRepository;

	@Autowired
	private FieldMapper fieldMapper;

	public FieldServiceImpl() {
		super(Field.class);

	}

	@Override
	public FieldDto save(FieldDto fieldDto) {
		if (fieldDto != null) {

			Field existField = findBySlug(fieldDto.getSlug());

			if (existField != null && !fieldDto.getId().equals(existField.getId())) {
				throw new EntityAlreadyExistsException(
						String.format("Ce champ avec le nom <%s> est toujours exsite ", fieldDto.getName()));
			}
		}

		fieldDto.setSlug(fieldDto.getName());

		Field field = fieldRepository.save(fieldMapper.dtoToEntity(fieldDto));

		return fieldMapper.entityToDto(field);

	}

	@Override
	public Predicate findByField(SearchCriteria criteria, Root<?> root, Layer layer) {
		return null;
	}

	@Override
	public Optional<Field> findByNameAndLayer(String name, Layer layer) {
		return fieldRepository.findByNameAndLayer(name, layer);
	}

	@Override
	public Optional<Field> findFieldBySlugAndLayer(String slug, Layer layer) {
		return fieldRepository.findBySlugAndLayer(slug, layer);
	}

	@Override
	public Field findBySlug(String slug) {
		Optional<Field> field = fieldRepository.findBySlug(slug);
		return field.isPresent() ? field.get() : null;
	}

	@Override
	public boolean findBySlugAndLayer(String slug, Layer layer) {
		return fieldRepository.findBySlugAndLayer(slug, layer).isPresent();
	}
}
