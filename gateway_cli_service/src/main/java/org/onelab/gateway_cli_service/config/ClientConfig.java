package org.onelab.gateway_cli_service.config;

import feign.RequestInterceptor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Configuration
public class ClientConfig {

    @Setter
    private static String token;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String url = requestTemplate.url();

            if (url.contains("/api/users/login") || url.contains("/api/users/register")) {
                return;
            }

            if (token == null || token.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Please log in first.");
            }

            requestTemplate.header("Authorization", "Bearer " + token);
        };
    }

}

