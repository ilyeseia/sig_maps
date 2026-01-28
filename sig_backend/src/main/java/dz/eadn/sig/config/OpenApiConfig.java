package dz.eadn.sig.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import dz.eadn.sig.util.AuditorAwareImpl;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class OpenApiConfig {

	@Bean
	public AuditorAware<String> auditorAware() {
		return new AuditorAwareImpl();
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().components(new Components()).info(new Info().title("SIG-EADN Application API")
				.description("SIG-EADN RESTful service using springdoc-openapi and OpenAPI 3.").version("1.0"));
	}

}
