package com.example.UserManagement.controller;

import com.example.UserManagement.dto.CreateUserDto;
import com.example.UserManagement.dto.UserDto;
import com.example.UserManagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    // Constructor injection makes the dependency explicit and keeps it immutable.
    public UserController(UserService userService) {
        this.userService = userService;
    }

//    // GET /users
//    // Returns all users.
//    @GetMapping
//    public ResponseEntity<List<UserDto>> getUsers() {
//        return ResponseEntity.ok(userService.getUsers());
//    }

    // GET /users  Paginated api
    // Returns all users.
    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(@RequestParam(required = false) String search, Pageable pageable) {
        return ResponseEntity.ok(userService.getUsers(search, pageable));
    }

    // GET /users/{id}
    // Returns a single user. If the user doesn't exist, the service throws
    // ResourceNotFoundException, which is converted to HTTP 404 by the exception handler.
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // PUT /users/{id}
    // Replaces the existing user with the supplied data.
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody CreateUserDto updateUserDto) {

        UserDto updatedUser = userService.updateUser(id, updateUserDto);

        return ResponseEntity.ok(updatedUser);
    }

    // PATCH /users/{id}
    // Partially updates an existing user.
    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> patchUser(@PathVariable Long id, @Valid @RequestBody CreateUserDto patchUserDto) {

        UserDto updatedUser = userService.patchUser(id, patchUserDto);

        return ResponseEntity.ok(updatedUser);
    }

    // DELETE /users/{id}
    // Successfully deleting a resource returns 204 No Content.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {

        userService.deleteUserById(id);

        return ResponseEntity.noContent().build();
    }
}
