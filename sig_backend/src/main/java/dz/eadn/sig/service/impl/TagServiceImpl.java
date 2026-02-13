package dz.eadn.sig.service.impl;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dz.eadn.sig.dto.CustomJoinFilter;
import dz.eadn.sig.dto.TagDto;
import dz.eadn.sig.exceptions.EntityAlreadyExistsException;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.mapper.TagMapper;
import dz.eadn.sig.model.Tag;
import dz.eadn.sig.repository.EntityElementRepository;
import dz.eadn.sig.repository.LayerRepository;
import dz.eadn.sig.repository.MapRepository;
import dz.eadn.sig.repository.TagRepository;
import dz.eadn.sig.service.TagService;
import dz.eadn.sig.service.common.impl.CommonServiceImpl;

/**
 * @author Achrouf Abdenour & Ameur LAMOUR
 *
 */
@Service
public class TagServiceImpl extends CommonServiceImpl<Tag, TagDto> implements TagService {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private TagMapper tagMapper;

	@Autowired
	LayerRepository layerRepository;

	@Autowired
	MapRepository mapRepository;

	@Autowired
	EntityElementRepository entiteElementRepository;

	public TagServiceImpl() {
		super(Tag.class);
	}

	@Override
	public void delete(UUID id) {
		Tag tag = findById(id);

		if (tag == null) {
			throw new EntityNotFoundException("Impossible de supprimer l'entité " + domainClass.getSimpleName());
		}

		tagRepository.delete(tag);
	}

	@Override
	public List<Object> findAllByType(CustomJoinFilter customJoinFilter) {
		return tagRepository.findAllByType(customJoinFilter);
	}

	@Override
	public Tag findByName(String name) {
		return tagRepository.findByName(name);
	}

	@Override
	public TagDto save(TagDto tagDto) {

		if (tagDto != null) {

			Tag existtag = tagRepository.findByName(tagDto.getName());

			if (existtag != null && !tagDto.getId().equals(existtag.getId())) {
				throw new EntityAlreadyExistsException(
						String.format("tag avec le nom <%s> est toujours exsite ", tagDto.getName()));
			}
		}

		Tag tag = tagRepository.save(tagMapper.dtoToEntity(tagDto));

		return tagMapper.entityToDto(tag);
	}

}
