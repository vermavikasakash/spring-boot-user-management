package com.example.UserManagement.service;

import com.example.UserManagement.dto.CreateUserDto;
import com.example.UserManagement.dto.UserDto;
import com.example.UserManagement.entity.User;
import com.example.UserManagement.exception.ResourceNotFoundException;
import com.example.UserManagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Creates a new User entity from the incoming request DTO.
    public UserDto register(CreateUserDto createUserDto) {

        String encodedPassword = passwordEncoder.encode(createUserDto.getPassword());

        User user = new User(createUserDto.getName(), createUserDto.getEmail(), encodedPassword);

        // save() persists the entity and returns the managed/saved entity,
        // including the database-generated ID.
        User savedUser = userRepository.save(user);

        return new UserDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
    }

    // Get user by email id
    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
