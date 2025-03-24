package org.onelab.order_worker.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeignTokenInterceptor implements RequestInterceptor {

    private static final ThreadLocal<String> tokenHolder = new ThreadLocal<>();

    public static void setToken(String token) {
        tokenHolder.set(token);
    }

    public static void clearToken() {
        tokenHolder.remove();
    }

    @Override
    public void apply(RequestTemplate template) {
        String token = tokenHolder.get();
        if (token != null) {
            template.header("Authorization", token);
        }
    }
}
