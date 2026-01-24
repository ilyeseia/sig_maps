package dz.eadn.sig.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dz.eadn.sig.dto.CommonFilter;
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.dto.SessionDto;
import dz.eadn.sig.dto.UserDto;
import dz.eadn.sig.mapper.UserMapper;
import dz.eadn.sig.model.User;
import dz.eadn.sig.security.RedisUtil;
import dz.eadn.sig.service.SessionService;
import dz.eadn.sig.service.UserService;

@Service
public class SessionServiceImpl implements SessionService {

	@Autowired
	private UserService userService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private RedisUtil redisUtil;

	@Override
	public List<SessionDto> findByAdvancedFilter(CommonFilter filter, Integer page, Integer limit, String sort,
			String dir) {
		PageDto<UserDto> usersPage = userService.findAllByFilter(filter, page, limit, sort, dir);

		if (usersPage != null) {
			List<User> users = userMapper.dtosToEntitys(usersPage.getContent());
			return createSessionsFromUsers(users);
		} else
			return new ArrayList<SessionDto>();
	}

	private List<SessionDto> createSessionsFromUsers(List<User> users) {
		List<SessionDto> result = new ArrayList<SessionDto>();

		for (User user : users) {
			SessionDto sessionDto = new SessionDto();
			Set<String> userTokens = redisUtil.findConnectedUserTokens(user.getUsername());
			boolean tokenExpired = userTokens.isEmpty() ? false : true;
			sessionDto.setTokenExpired(tokenExpired);
			sessionDto.setEmail(user.getEmail());
			sessionDto.setAvatar(user.getAvatar());
			sessionDto.setUserName(user.getUsername());
			sessionDto.setUserName(user.getUsername());
			result.add(sessionDto);
		}

		return result;
	}

	@Override
	public PageDto<SessionDto> findAll(Integer page, Integer limit, String sort, String dir) {
		PageDto<SessionDto> dtos = new PageDto<SessionDto>();
		List<User> users = userService.findAll(page, limit, sort, dir);
		List<SessionDto> sessions = createSessionsFromUsers(users);
		dtos.setContent(sessions);
		dtos.setTotalElements(users.size());

		return dtos;
	}

	public long count() {
		return userService.count();
	}

	public void delete(String userName) throws Exception {
		redisUtil.srem(userName);
	}

	public void delete(String userName, String token) throws Exception {
		redisUtil.srem(userName, token);
	}
}
