package dz.eadn.sig.repository.custom;

import java.util.List;

import dz.eadn.sig.dto.CustomJoinFilter;

public interface CustomTagRepository {

	public List<Object> findAllByType(CustomJoinFilter customJoinFilter);

}
