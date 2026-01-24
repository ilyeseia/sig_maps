///**
// * 
// */
//package dz.eadn.sig.mapper;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import dz.eadn.sig.dto.UserNotificationDto;
//import dz.eadn.sig.model.UserNotification;
//import dz.eadn.sig.service.UserNotificationService;
//
///**
// * @author Achrouf Abdenour
// *
// */
//
//@Component
//public class UserNotificationMapper extends CommonMapper<UserNotification, UserNotificationDto> {
//
//	@Autowired
//	private UserNotificationService userNotificationService;
//
//	@Override
//	protected UserNotificationDto mapEntityToDto(UserNotification entity) {
//		UserNotificationDto userNotificationDto = new UserNotificationDto();
//		userNotificationDto.setLink(entity.getLink());
//		userNotificationDto.setMessage(entity.getMessage());
//		userNotificationDto.setViewed(entity.getViewed());
//		userNotificationDto.setViewedDate(entity.getViewedDate());
//		return userNotificationDto;
//	}
//
//	@Override
//	protected UserNotification mapDtoToEntity(UserNotificationDto dto) {
//		UserNotification userNotifcation = userNotificationService.findById(dto.getId(), true);
//
//		if (userNotifcation == null) {
//			if (dto.getId() != null) {
//				return null;
//			}
//			userNotifcation = new UserNotification();
//		} else {
//			if (userNotifcation.getDeleted())
//				throw new RuntimeException("can't do operation on deleted user");
//		}
//
//		if (dto.getViewed() != null) {
//			userNotifcation.setViewed(dto.getViewed());
//		}
//
//		if (dto.getViewedDate() != null) {
//			userNotifcation.setViewedDate(dto.getViewedDate());
//		}
//
//		if (dto.getLink() != null) {
//			userNotifcation.setLink(dto.getLink());
//		}
//
//		if (dto.getMessage() != null) {
//			userNotifcation.setMessage(dto.getMessage());
//		}
//
//		return userNotifcation;
//	}
//
//}
