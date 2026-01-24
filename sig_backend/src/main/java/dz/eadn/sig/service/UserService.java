package dz.eadn.sig.service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;

import dz.eadn.sig.dto.CommonFilter;
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.dto.ProfileDto;
import dz.eadn.sig.dto.UserCompleteDto;
import dz.eadn.sig.dto.UserDto;
import dz.eadn.sig.dto.UserSimpleDto;
import dz.eadn.sig.model.User;
import dz.eadn.sig.service.common.CommonService;

/**
 * @author Achrouf Abdenour & Ameur LAMOUR
 *
 */

public interface UserService extends CommonService<User, UserDto> {

	public User findByUsername(String username);

	public ProfileDto getCurrentUser(String username, Integer page, Integer limit, String sort, String dir);

	public User findByEmail(String mail);

	public void sendMail(User user, Map<String, Object> properties, String templateName, String subject)
			throws MessagingException, IOException;

	public String generatePassword();

	public void resetPassword(UUID userId, String newPassword);

	public boolean isAdministrateur(String username);

	public PageDto<UserSimpleDto> findAllUsersSimpleByFilter(CommonFilter filter, Integer page, Integer limit,
			String sort, String dir);

	public PageDto<UserCompleteDto> findAllUsersCompleteByFilter(CommonFilter filter, Integer page, Integer limit,
			String sort, String dir);

	public PageDto<UserCompleteDto> getAllUsersCompleteByPage(Integer page, Integer limit, String sort, String dir);
	// public boolean checkValidity(String username);

}
