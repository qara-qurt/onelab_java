package org.onelab.restaurant_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/menus/**", "/api/dishes/**", "/api/orders/user/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/menus/**", "/api/dishes/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/menus/**", "/api/dishes/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/menus/**", "/api/dishes/**").hasAnyRole("MANAGER", "ADMIN")


                        .requestMatchers(HttpMethod.POST, "/api/menus/{menuId}/dishes/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/menus/{menuId}/dishes/**").hasAnyRole("MANAGER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/orders/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/orders/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/orders/**").hasAnyRole("MANAGER", "ADMIN")


                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
