package dz.eadn.sig.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author A.LAMOUR
 *
 */

@Configuration
public class SpringConfig {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
