package com.example.UserManagement.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

//    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 1. Configure CSRF
        http.csrf(AbstractHttpConfigurer::disable);

        // 2. Configure session management
        http.sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });

        // 3. Configure authorization rules
        http.authorizeHttpRequests(auth -> {

            auth.requestMatchers(
                    "/api/v1/auth/login",
                    "/api/v1/auth/register"
            ).permitAll();

            auth.requestMatchers(
                    HttpMethod.GET,
                    "/api/v1/products/**"
            ).permitAll();

            auth.requestMatchers(
                    "/api/v1/admin/**"
            ).hasRole("ADMIN");

            auth.requestMatchers(
                    "/api/v1/users/**"
            ).hasAnyRole("USER", "ADMIN");

            auth.anyRequest().authenticated();
        });

        // 4. Add our JWT filter
//        http.addFilterBefore(
//                jwtAuthenticationFilter,
//                UsernamePasswordAuthenticationFilter.class
//        );

        // 5. Build the final SecurityFilterChain
        return http.build();
    }
}
