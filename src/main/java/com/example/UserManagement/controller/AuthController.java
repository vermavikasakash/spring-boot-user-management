package com.example.UserManagement.controller;

import com.example.UserManagement.dto.CreateUserDto;
import com.example.UserManagement.dto.LoginRequestDto;
import com.example.UserManagement.dto.LoginResponseDto;
import com.example.UserManagement.dto.UserDto;
import com.example.UserManagement.entity.User;
import com.example.UserManagement.security.JwtService;
import com.example.UserManagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody CreateUserDto dto) {

        UserDto user = userService.createUser(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal(); // get the authenticated user

        User user = userService.getUserByEmail(request.getEmail());
        String token = jwtService.generateToken(user); // generate the token

        return ResponseEntity.ok(new LoginResponseDto(token));
    }
}