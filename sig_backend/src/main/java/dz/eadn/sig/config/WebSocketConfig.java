/**
 * 
 */
package dz.eadn.sig.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author Achrouf Abdenour
 *
 */
@Configuration
@EnableWebSocketMessageBroker
@PropertySource("classpath:global.properties")
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Value("${eadn.sig.front.server}")
	private String FRONT_SERVER_ADDRESS;
	
	@Value("${eadn.sig.domaines_autorises}")
	private String corssOrigin;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/notification");
		registry.setApplicationDestinationPrefixes("/swns");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/notifications").setAllowedOrigins(corssOrigin)
				.setHandshakeHandler(new CustomHandshakeHandler()).withSockJS();
	}
}
