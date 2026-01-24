package dz.eadn.sig.repository;

import org.springframework.context.annotation.Primary;

import dz.eadn.sig.model.UserLog;
import dz.eadn.sig.repository.common.CommonRepository;

@Primary
public interface UserLogRepository extends CommonRepository<UserLog> {
	public UserLog findByToken(String token);
}
