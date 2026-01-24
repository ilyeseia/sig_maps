package dz.eadn.sig.security;

import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import dz.eadn.sig.model.User;
import dz.eadn.sig.service.UserService;
import dz.eadn.sig.service.impl.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ameur LAMOUR && Achrouf Abdenour
 *
 */
@PropertySource("classpath:global.properties")
@Slf4j
@Component
public class JwtUtils {

	@Value("${eadn.sig.jwtSecret}")
	private String jwtSecret;

	@Value("${eadn.sig.jwtExpirationMs}")
	private int jwtExpirationMs;

	@Value("${eadn.sig.jwtRefreshExpirationMs}")
	private int jwtRefreshExpirationMs;

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private UserService userService;

	public String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7, headerAuth.length());
		}

		return null;
	}

	public String generateJwtToken(Authentication authentication, int expiration) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		String token = Jwts.builder().setSubject((userPrincipal.getUsername()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();

		return token;
	}

	public String generateRefreshToken(Authentication authentication) {
		return generateJwtToken(authentication, jwtRefreshExpirationMs);
	}

	public String generateJwtToken(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String token = generateJwtToken(authentication, jwtExpirationMs);
		redisUtil.sadd(userPrincipal.getUsername(), token);
		return token;
	}

	public Date getExpiration(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getExpiration();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public void invalidateRelatedTokens(String token) {
		String userName = getUserNameFromJwtToken(token);
		redisUtil.srem(userName);
	}

	public void invalidateRelatedToken(String token) {
		String userName = getUserNameFromJwtToken(token);
		redisUtil.srem(userName, token);
	}

	public boolean validateSubject(String token) {
		String userName = getUserNameFromJwtToken(token);

		User user = userService.findByUsername(userName);
		if (user.getEnabled()) {
			return true;
		}

		return false;
	}

	public boolean validateJwtRefreshToken(String refreshToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(refreshToken);
			return validateSubject(refreshToken);
		} catch (SignatureException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			String username = claims.getBody().getSubject();
			return redisUtil.sismember(username, authToken);
		} catch (SignatureException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}
}
