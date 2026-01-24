
package dz.eadn.sig.service;

import dz.eadn.sig.dto.CommonFilter;
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.dto.SessionDto;

import java.util.List;

public interface SessionService {
	PageDto<SessionDto> findAll(Integer page, Integer limit, String sort, String dir);

	List<SessionDto> findByAdvancedFilter(CommonFilter filter, Integer page, Integer limit, String sort, String dir);

	long count();

	void delete(String userName) throws Exception;

	void delete(String userName, String token) throws Exception;
}
