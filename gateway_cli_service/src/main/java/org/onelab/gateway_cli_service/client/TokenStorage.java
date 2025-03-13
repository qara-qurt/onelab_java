package org.onelab.gateway_cli_service.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class TokenStorage {
    private String token;
}
