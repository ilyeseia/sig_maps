package dz.eadn.sig.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.opencsv.exceptions.CsvValidationException;
import dz.eadn.sig.dto.FieldDto;
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.exceptions.EntityAlreadyExistsException;
import dz.eadn.sig.exceptions.GlobalException;
import dz.eadn.sig.mapper.CommonModelMapper;
import dz.eadn.sig.mapper.ResourceValueMapper;
import dz.eadn.sig.model.Field;
import dz.eadn.sig.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;

import dz.eadn.sig.dto.ResourceValueDto;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.model.ResourceValue;
import dz.eadn.sig.repository.ResourceValueRepository;
import dz.eadn.sig.service.ResourceValueService;
import dz.eadn.sig.service.common.impl.CommonServiceImpl;

import javax.transaction.Transactional;

/**
 * @author Achrouf Abdenour, LOKBANI Chouaib
 *
 */
@Service
public class ResourceValueServiceImpl extends CommonServiceImpl<ResourceValue, ResourceValueDto>
		implements ResourceValueService {

	@Autowired
	private ResourceValueRepository resourceValueRepository;

	@Autowired
	private CommonModelMapper<?, ?> cModelMapper;

	@Autowired
	private ResourceValueMapper resourceValueMapper;

	@Autowired
	ResourceRepository resourceRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public ResourceValueServiceImpl() {
		super(ResourceValue.class);
	}

	@Transactional
	@Override
	public ResourceValueDto save(ResourceValueDto resourceValueDto) throws GlobalException {
		try{
			ResourceValue resourceValue = resourceValueRepository.save(resourceValueMapper.dtoToEntity(resourceValueDto));
			if (resourceValueDto != null && resourceValueDto.getId() != null) {
				List<Map<String, Object>> affectedFields = jdbcTemplate.queryForList("select  f.slug,  f.layer_id from sig.field f where f.resource_id = '" + resourceValueDto.getResourceId() +"'\n");
				affectedFields.forEach(f -> {
					jdbcTemplate.execute(
							"update sig.entity_element e  SET properties =  JSONB_SET(e.properties, '{"+ f.get("slug") +"}', '\"" + resourceValueDto.getId() + ":" + resourceValueDto.getValue().replace("'", "''")+ "\"')\n" +
									"from sig.field f\n" +
									"where e.layer_entity_element = '" + f.get("layer_id") +"'  and\n" +
									"      split_part(e.properties->>f.slug, ':', 1) =  '" + resourceValueDto.getId() + "'"
					);
				});
			}

			return resourceValueMapper.entityToDto(resourceValue);
		}catch (Exception e){
			throw new GlobalException("une erreur inattendue s'est produite ?");
		}
	}

	@Override
	public List<ResourceValue> findAllByResouceIdAndParentId(UUID resourceId, UUID parentId) {

		String query = "SELECT * FROM sig.resource_value WHERE resource_id ='" + resourceId + "' AND parent_id ='"
				+ parentId + "'";

		List<ResourceValue> result = jdbcTemplate.query(query,
				new BeanPropertyRowMapper<ResourceValue>(ResourceValue.class));

		return result;
	}

	@Override
	public List<List<String>> readResourceValues(InputStream is) throws IOException {

		List<List<String>> resourceValues = new ArrayList<List<String>>();

		try (CSVReader csvReader = new CSVReader(new InputStreamReader(is));) {
			String[] values = null;
			while ((values = csvReader.readNext()) != null) {
				resourceValues.add(Arrays.asList(values));
			}
		} catch (CsvValidationException e) {
			throw new RuntimeException(e);
		}
		return resourceValues;
	}


	@Transactional
	@Override
	public void delete(UUID id) {

		ResourceValue resourceValue = findById(id);

		if(resourceValueRepository.countAllByParentId(id) > 0){
			throw new GlobalException("Cette valeur a déjà des enfants, supprimez-les d'abord");
		}

		if (resourceValue == null) {
			throw new EntityNotFoundException("Impossible de supprimer l'entité " + domainClass.getSimpleName());
		}

		resourceValueRepository.delete(resourceValue);
		resourceValueRepository.deleteAllByParentId(id);
	}


	@Override
	public void deleteAll(Collection<ResourceValue> instances) {
		resourceValueRepository.deleteAll(instances);
	}

	@Override
	public PageDto<ResourceValueDto> findByValueAndResource(String resourceId, String searchedValue, Integer page, Integer limit, String sort, String dir) {
		PageDto<ResourceValueDto> pageDto = null;
		pageDto = new PageDto<>();
		Sort sortDir = null;

		if (dir.equals("asc"))
			sortDir = Sort.by(sort).ascending();
		else
			sortDir = Sort.by(sort).descending();

		Pageable pageable = PageRequest.of(page, limit, sortDir);

		Page<ResourceValue> resourceValuesPage = resourceValueRepository.findAllByResource_IdAndValueContainsIgnoreCase(UUID.fromString(resourceId),searchedValue, pageable);
		List<ResourceValueDto> resourceValuesDto = cModelMapper.mapList(resourceValuesPage.getContent(), ResourceValueDto.class);
		pageDto.setContent(resourceValuesDto);
		pageDto.setTotalElements(resourceValuesPage.getTotalElements());

		return pageDto;
	}

	@Override
	public PageDto<ResourceValueDto> findByValueAndResourceAndParent(String resourceId, String rvParentId, String searchedValue, Integer page, Integer limit, String sort, String dir) {
		PageDto<ResourceValueDto> pageDto = null;
		pageDto = new PageDto<>();
		Sort sortDir = null;

		if (dir.equals("asc"))
			sortDir = Sort.by(sort).ascending();
		else
			sortDir = Sort.by(sort).descending();

		Pageable pageable = PageRequest.of(page, limit, sortDir);

		Page<ResourceValue> resourceValuesPage = resourceValueRepository.findAllByResource_IdAndParentIdAndValueContainsIgnoreCase(UUID.fromString(resourceId),UUID.fromString(rvParentId), searchedValue, pageable);
		List<ResourceValueDto> resourceValuesDto = cModelMapper.mapList(resourceValuesPage.getContent(), ResourceValueDto.class);
		pageDto.setContent(resourceValuesDto);
		pageDto.setTotalElements(resourceValuesPage.getTotalElements());

		return pageDto;
	}

	@Override
	public boolean checkExistenceByParent(UUID parentId, String value, UUID rvId) {
		return resourceValueRepository.countByParentIdAndValueIgnoreCaseAndIdNot(parentId, value, rvId) > 0;
	}

	@Override
	public boolean checkExistenceByResource(UUID resourceId, String value, UUID rvId) {
		return resourceValueRepository.countByResource_IdAndValueIgnoreCaseAndIdNot(resourceId, value, rvId) > 0;
	}

	@Override
	public boolean checkExistenceByResource(UUID resourceId, UUID parentId, String value) {
		return parentId == null ?
				resourceValueRepository.countByResource_IdAndValueIgnoreCase(resourceId, value) > 0 :
				resourceValueRepository.countByResource_IdAndParentIdAndValueIgnoreCase(resourceId, parentId, value) > 0;
	}

	@Override
	public HashMap<String, List<ResourceValueDto>>  importResourceValues(List<ResourceValueDto> resourceValueDtoList, boolean withMissingValue) {
		if(resourceValueDtoList != null && !resourceValueDtoList.isEmpty()) {
			HashMap<String, List<ResourceValueDto>> addedDeletedRV = new HashMap<>();
			addedDeletedRV.put("addedValues", new ArrayList<>());
			addedDeletedRV.put("skippedValues", new ArrayList<>());
			if(withMissingValue){
				resourceValueDtoList.forEach(rv -> {
					if (!checkExistenceByResource(rv.getResourceId(), rv.getParentId(), rv.getValue())) {
						ResourceValue addedRV = resourceValueRepository.save(resourceValueMapper.dtoToEntity(rv));
						addedDeletedRV.get("addedValues").add(resourceValueMapper.entityToDto(addedRV));
					}
				});
			}
			addedDeletedRV.get("skippedValues").addAll(mapper.entitysToDtos(resourceValueRepository.findAllByResource_Id(resourceValueDtoList.get(0).getResourceId())));
			return addedDeletedRV;
		}
		return null;
	}

	@Transactional
	@Override
	public List<UUID> deleteAllResourceValuesByResource(UUID uuid) {
		List<UUID> deletedRV = new ArrayList<>();
		resourceValueRepository.findAllByResource_Id(uuid).forEach(r -> {
			if(resourceValueRepository.countAllByParentId(r.getId()) == 0){
				deletedRV.add(r.getId());
				resourceValueRepository.delete(r);
			}
		});
		return deletedRV;
	}

	@Transactional
	@Override
	public List<UUID> deleteAllByParentId(UUID uuid) {
		List<UUID> deletedRV = new ArrayList<>();
		AtomicBoolean allDeleted = new AtomicBoolean(true);
		resourceValueRepository.findAllByParentId(uuid).forEach(r -> {
			if(resourceValueRepository.countAllByParentId(r.getId()) == 0){
				deletedRV.add(r.getId());
				resourceValueRepository.delete(r);
			}
		});
		return deletedRV;
	}
}
