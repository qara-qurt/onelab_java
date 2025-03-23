package org.onelab.camunda_service.config;

import feign.RequestInterceptor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Setter
    private static String token;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            if (token != null) {
                requestTemplate.header("Authorization", token);
            }
        };
    }

}
