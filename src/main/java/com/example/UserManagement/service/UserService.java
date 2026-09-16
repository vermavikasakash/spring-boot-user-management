package com.example.UserManagement.service;

import com.example.UserManagement.dto.CreateUserDto;
import com.example.UserManagement.dto.UserDto;
import com.example.UserManagement.entity.User;
import com.example.UserManagement.exception.ResourceNotFoundException;
import com.example.UserManagement.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Fetches all users and converts database entities into API response DTOs.
//    public List<UserDto> getUsers() {
//
//        List<User> users = userRepository.findAll();
//        List<UserDto> userDtoList = new ArrayList<>();
//
//        for (User user : users) {
//            userDtoList.add(new UserDto(user.getId(), user.getName(), user.getEmail()));
//        }
//        return userDtoList;
//    }

    // Fetches all paginated users and converts database entities into API response DTOs.
    public Page<UserDto> getUsers(String search, Pageable pageable) {

        Page<User> users;

        if (search != null && !search.isBlank()) {
            users = userRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return users.map(user -> new UserDto(user.getId(), user.getName(), user.getEmail()));
    }

    // Fetches a user by ID.
    // A missing user is treated as a resource-not-found condition.
    public UserDto getUserById(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }


    // Replaces the existing user's data.
    @Transactional
    public UserDto updateUser(Long id, CreateUserDto updateUserDto) {

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // PUT replaces the complete resource,
        // so we update all fields rather than checking for null.
        user.setName(updateUserDto.getName());
        user.setEmail(updateUserDto.getEmail());

        // save() is not strictly required because the entity is managed
        // and JPA dirty checking will persist the changes.
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    // @Transactional allows JPA dirty checking to persist changes
    // when the transaction is committed.
    @Transactional
    public UserDto patchUser(Long id, CreateUserDto patchUserDto) {

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // PATCH semantics: update only fields supplied by the client.
        if (patchUserDto.getName() != null) {
            user.setName(patchUserDto.getName());
        }

        if (patchUserDto.getEmail() != null) {
            user.setEmail(patchUserDto.getEmail());
        }

        // save() is not strictly required here because the entity is managed
        // inside the transaction and JPA dirty checking detects these changes.
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public void deleteUserById(Long id) {

        // Check first so that deleting a non-existent user produces 404
        // instead of silently behaving like a successful delete.
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
    }
}
