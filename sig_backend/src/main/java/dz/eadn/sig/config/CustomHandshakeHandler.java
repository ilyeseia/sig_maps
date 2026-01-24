/**
 * 
 */
package dz.eadn.sig.config;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

/**
 * @author Achrouf Abdenour
 *
 */


class CustomHandshakeHandler extends DefaultHandshakeHandler {

	// Custom class for storing principal
	@Override
	protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
			Map<String, Object> attributes) {
		// Generate principal with UUID as name
		String requestString = request.getURI().toString();

		if (requestString == null || !requestString.contains("=") || requestString.equals("")) {
			return null;
		}

		String jwt = requestString.split("=")[1];

		return new StompPrincipal(jwt);
	}
}