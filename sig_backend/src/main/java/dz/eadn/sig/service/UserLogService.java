package dz.eadn.sig.service;

import javax.servlet.http.HttpServletRequest;

import dz.eadn.sig.dto.UserLogDto;
import dz.eadn.sig.model.UserLog;
import dz.eadn.sig.service.common.CommonService;

/**
 * @author Achrouf Abdenour & Ameur LAMOUR
 *
 */
public interface UserLogService extends CommonService<UserLog,UserLogDto> {

	public UserLog findByToken(String token);

	public String getReferer(HttpServletRequest request);
	
	public String getClientBrowserVersion(HttpServletRequest request);

	public String getClientIpAddr(HttpServletRequest request);

	public String getClientOS(HttpServletRequest request);

	public String getClientBrowser(HttpServletRequest request);

	public String getUserAgent(HttpServletRequest request);
}
