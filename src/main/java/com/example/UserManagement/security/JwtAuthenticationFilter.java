package com.example.UserManagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // We'll implement this next.
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String jwt = authHeader.substring(7);

            String email = jwtService.extractUsername(jwt);

            System.out.println("Email from JWT: " + email);
            // converts your application User into Spring Security's
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            /*
            creates an Authentication object containing :
            Principal     → userDetails
            Credentials   → null
            Authorities   → ROLE_ADMIN / ROLE_USER
            Note : We use null for credentials because we don't want to store the password in the SecurityContext
            */
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        // for future checks the Authentication (stored in the SecurityContext)
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // I've finished processing this filter; continue to the next filter
        filterChain.doFilter(request, response);
    }
}
