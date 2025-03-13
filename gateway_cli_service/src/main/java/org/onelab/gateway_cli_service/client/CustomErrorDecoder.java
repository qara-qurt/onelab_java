package org.onelab.gateway_cli_service.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        String errorMessage = "Server error";

        try {
            if (response.body() != null) {
                String responseBody = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
                errorMessage = extractErrorMessage(responseBody);
            }
        } catch (IOException ignored) {}

        return switch (status) {
            case UNAUTHORIZED -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Please log in first.");
            case FORBIDDEN -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden: You do not have access.");
            case CONFLICT -> new ResponseStatusException(HttpStatus.CONFLICT, "Conflict: " + errorMessage);
            case BAD_REQUEST -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request: " + errorMessage);
            case NOT_FOUND -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found: " + errorMessage);
            default -> defaultDecoder.decode(methodKey, response);
        };
    }

    private String extractErrorMessage(String json) {
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode messagesNode = rootNode.path("messages");

            if (messagesNode.isArray() && messagesNode.size() > 0) {
                return String.join(", ", objectMapper.convertValue(messagesNode, List.class));
            }
        } catch (Exception e) {
            return "Unable to process server error";
        }
        return "Unknown error";
    }
}
