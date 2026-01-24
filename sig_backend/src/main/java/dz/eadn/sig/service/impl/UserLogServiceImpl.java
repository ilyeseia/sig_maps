package dz.eadn.sig.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dz.eadn.sig.dto.UserLogDto;
import dz.eadn.sig.model.UserLog;
import dz.eadn.sig.repository.UserLogRepository;
import dz.eadn.sig.service.UserLogService;
import dz.eadn.sig.service.common.impl.CommonServiceImpl;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;

/**
 * @author Achrouf Abdenour
 *
 */
@Service
public class UserLogServiceImpl extends CommonServiceImpl<UserLog, UserLogDto> implements UserLogService {

	@Autowired
	private UserLogRepository userLogRepository;

	public String getClientBrowserVersion(HttpServletRequest request) {
		UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
		Version version = userAgent.getBrowserVersion();
		if (version == null)
			return "Unknown";
		else
			return userAgent.getBrowserVersion().getVersion();
	}

	public UserLogServiceImpl() {
		super(UserLog.class);
	}

	public String getReferer(HttpServletRequest request) {
		final String referer = request.getHeader("referer");
		return referer;
	}

	public String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public String getClientOS(HttpServletRequest request) {
		UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
		return userAgent.getOperatingSystem().getName();
	}

	public String getClientBrowser(HttpServletRequest request) {
		UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
		return userAgent.getBrowser().getName();
	}

	public String getUserAgent(HttpServletRequest request) {
		return request.getHeader("User-Agent");
	}

	@Override
	public UserLog findByToken(String token) {
		return userLogRepository.findByToken(token);
	}
}
