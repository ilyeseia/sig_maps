package dz.eadn.sig.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfiguration {


    @Value("${geoserver.rest.user}")
    private String geoServerUser;

    @Value("${geoserver.rest.password}")
    private String geoServerPassword;

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor(geoServerUser, geoServerPassword);
    }
}
