package dz.eadn.sig.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import dz.eadn.sig.dto.NotificationSimpleDto;
import dz.eadn.sig.dto.SessionDto;
import dz.eadn.sig.dto.UserNotificationDto;
import dz.eadn.sig.model.Group;
import dz.eadn.sig.model.User;
import dz.eadn.sig.model.UserNotification;
import dz.eadn.sig.repository.UserNotificationRepository;
import dz.eadn.sig.security.RedisUtil;
import dz.eadn.sig.service.GroupService;
import dz.eadn.sig.service.NotificationMessageService;

/**
 * @author LAMOUR AMEUR
 *
 */
@Service
public class NotificationMessageServiceImpl implements NotificationMessageService {

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private UserNotificationRepository userNotificationRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private GroupService groupService;

	@Override
	public void sendNotificationMessage(NotificationSimpleDto notification) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		UserNotificationDto userNotificationDto = null;

		List<SessionDto> sessionDtos = redisUtil.findAllSessions();

		Group group = groupService.findByName("ROLE_ADMIN");

		List<User> users = new ArrayList<>();

		if (notification.getUsers() != null) {
			users.addAll(notification.getUsers());
		}

		if (group != null) {
			users.addAll(group.getUsers());
		}

		List<User> distinctUsers = users.stream().filter(distinctByKey(u -> u.getUsername()))
				.collect(Collectors.toList());

		// save the notification in database
		if (distinctUsers != null) {
			for (User user : distinctUsers) {

				if (!user.getUsername().equals(authentication.getName())) {

					UserNotification userNotification = new UserNotification();

					userNotification.setObject(notification.getObject().toString());
					userNotification.setLevel(notification.getLevel().toString());
					userNotification.setOperation(notification.getOperation().toString());
					userNotification.setMessage(notification.getMessage());
					userNotification.setViewed(false);
					userNotification.setUser(user);

					userNotification = userNotificationRepository.save(userNotification);
					userNotificationDto = modelMapper.map(userNotification, UserNotificationDto.class);

					Set<SessionDto> targetSessions = sessionDtos.stream()
							.filter(session -> session.getUserName().equals(user.getUsername()))
							.collect(Collectors.toSet());

					String destination = "/notification/" + notification.getDestination();

					if (targetSessions != null) {
						for (SessionDto sessionDto : targetSessions) {
							notification.getSystemNotification().setUserNotificationDto(userNotificationDto);
							messagingTemplate.convertAndSendToUser(sessionDto.getToken(), destination,
									notification.getSystemNotification());
						}
					}

				}

			}
		}

	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

}
