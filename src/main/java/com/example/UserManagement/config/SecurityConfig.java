package com.example.UserManagement.config;

import com.example.UserManagement.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 1. Configure CSRF
        // // http.csrf(AbstractHttpConfigurer::disable);
        http.csrf(csrfConfig -> csrfConfig.disable()); // above line and this both are same

        // 2. Configure session management
        http.sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });

        // 3. Configure authorization rules
        http.authorizeHttpRequests(auth -> {

            auth.requestMatchers("/api/v1/auth/login", "/api/v1/auth/register").permitAll();

            auth.requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll();

            auth.requestMatchers(HttpMethod.GET, "/api/v1/users/**").permitAll();

            auth.requestMatchers("/api/v1/admin/**").hasRole("ADMIN");

            auth.requestMatchers("/api/v1/users/**").hasAnyRole("USER", "ADMIN");

            auth.anyRequest().authenticated();
        });
        // 4. Add our JWT filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 5. Build the final SecurityFilterChain
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }
}
