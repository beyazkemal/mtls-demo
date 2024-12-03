package com.kemalbeyaz.mtls.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Kemal Beyaz
 * @date 3.12.2024
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(CsrfConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .requiresChannel(channel -> channel
                        .anyRequest().requiresSecure() // HTTPS zorunlu
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            authException.printStackTrace();
                            response.sendError(403, "mTLS gereklidir."); // mTLS olmayanlara 403
                        })
                );
        return http.build();
    }
}
