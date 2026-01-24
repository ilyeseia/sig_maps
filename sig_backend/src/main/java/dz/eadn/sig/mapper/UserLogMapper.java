package dz.eadn.sig.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.UserLogDto;
import dz.eadn.sig.model.UserLog;
import dz.eadn.sig.service.UserLogService;

/**
 * @author Achrouf Abdenour
 *
 */
@Component
public class UserLogMapper extends CommonMapper<UserLog, UserLogDto> {

	@Autowired
	private UserLogService userLogService;

	@Override
	protected UserLogDto mapEntityToDto(UserLog entity) {
		UserLogDto userLogDto = new UserLogDto();
		userLogDto.setUsername(entity.getUsername());
		userLogDto.setUserIp(entity.getUserIp());
		userLogDto.setLoginDate(entity.getLoginDate());
		userLogDto.setLogoutDate(entity.getLogoutDate());
		userLogDto.setBrowserName(entity.getBrowserName());
		userLogDto.setBrowserVersion(entity.getBrowserVersion());
		userLogDto.setToken(entity.getToken());
		userLogDto.setClientOS(entity.getClientOS());

		return userLogDto;
	}

	@Override
	protected UserLog mapDtoToEntity(UserLogDto dto) {
		UserLog userLog = userLogService.findById(dto.getId(), true);

		if (userLog == null) {
			if (dto.getId() != null) {
				return null;
			}
			userLog = new UserLog();
		} else {
			if (userLog.getDeleted())
				throw new RuntimeException("can't do operation on deleted user");
		}

		if (dto.getUsername() != null) {
			userLog.setUsername(dto.getUsername());
		}
		if (dto.getLoginDate() != null) {
			userLog.setLoginDate(dto.getLoginDate());
		}

		if (dto.getLogoutDate() != null) {
			userLog.setLogoutDate(dto.getLogoutDate());
		}

		if (dto.getUserIp() != null) {
			userLog.setUserIp(dto.getUserIp());
		}

		if (dto.getBrowserName() != null) {
			userLog.setBrowserName(dto.getBrowserName());
		}

		if (dto.getBrowserVersion() != null) {
			userLog.setBrowserVersion(dto.getBrowserVersion());
		}

		if (dto.getClientOS() != null) {
			userLog.setClientOS(dto.getClientOS());
		}

		if (dto.getToken() != null) {
			userLog.setToken(dto.getToken());
		}

		return userLog;
	}
}
