package dz.eadn.sig.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.SessionDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Achrouf Abdenour
 *
 */
@PropertySource("classpath:global.properties")
@Component
public class RedisUtil {
	private final JedisPool pool;

	@Value("${eadn.sig.jwtSecret}")
	private String jwtSecret;

	private static final int REDIS_PORT = 6379;
	private static final int REDIS_TIME_OUT = 60;

	public RedisUtil(@Value("${redis.host}") String redisHost, @Value("${redis.password}") String redisPassword) {

		pool = new JedisPool(new JedisPoolConfig(), redisHost, REDIS_PORT, REDIS_TIME_OUT, redisPassword);
	}

	public Set<String> findConnectedUserTokens(String user) {
		Set<String> tokens = new HashSet<String>();
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.smembers(user).forEach(token -> {
				boolean expired = false;
				try {
					Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getExpiration();
				} catch (ExpiredJwtException e) {
					expired = true;
				} finally {
					if (!expired)
						tokens.add(token);
				}

			});
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return tokens;
	}

	public List<SessionDto> findAllSessions() {
		List<SessionDto> sessions = new ArrayList<SessionDto>();
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			Set<String> keys = jedis.keys("*");
			for (String key : keys) {
				jedis.smembers(key).forEach(token -> {
					boolean expired = false;
					try {
						Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getExpiration();
					} catch (ExpiredJwtException e) {
						expired = true;
					} finally {
						if (!expired)
							sessions.add(new SessionDto(key, token));
					}

				});
			}
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return sessions;
	}

	public void sadd(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.sadd(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public void srem(String key) {
		Jedis jedis = null;
		try {
			Set<String> tokens = findConnectedUserTokens(key);
			jedis = pool.getResource();
			for (String token : tokens) {
				jedis.srem(key, token);
			}
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public void srem(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.srem(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public boolean sismember(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.sismember(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
}