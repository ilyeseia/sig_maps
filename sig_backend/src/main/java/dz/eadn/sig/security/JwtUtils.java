package dz.eadn.sig.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import jakarta.servlet.http.HttpServletRequest;

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
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT Utilities - Spring Boot 3.x / JJWT 0.12.x API
 * @author Ameur LAMOUR && Achrouf Abdenour
 * @updated 2026-01-28 - Migration to Jakarta EE and JJWT 0.12
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

	private SecretKey getSigningKey() {
		byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
		// Ensure key is at least 256 bits for HS512
		if (keyBytes.length < 64) {
			// Pad with zeros if key is too short (not recommended for production)
			byte[] paddedKey = new byte[64];
			System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
			keyBytes = paddedKey;
		}
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7);
		}

		return null;
	}

	public String generateJwtToken(Authentication authentication, int expiration) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder()
				.subject(userPrincipal.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(getSigningKey(), Jwts.SIG.HS512)
				.compact();
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
		return Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getExpiration();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
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
			Jwts.parser()
					.verifyWith(getSigningKey())
					.build()
					.parseSignedClaims(refreshToken);
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
			Jws<Claims> claims = Jwts.parser()
					.verifyWith(getSigningKey())
					.build()
					.parseSignedClaims(authToken);
			String username = claims.getPayload().getSubject();
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
