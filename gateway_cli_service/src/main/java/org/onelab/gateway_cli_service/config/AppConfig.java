package org.onelab.gateway_cli_service.config;

import feign.codec.ErrorDecoder;
import org.onelab.gateway_cli_service.client.CustomErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}
