package com.pixalive.backend.controller;

import com.pixalive.backend.dto.UserDto;
import com.pixalive.backend.model.User;
import com.pixalive.backend.response.ApiResponse;
import com.pixalive.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody UserDto userDto) {
        User createdUser = userService.createUser(userDto);
        return ResponseEntity.ok(new ApiResponse<>("User created successfully", createdUser));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>("Users fetched successfully", users));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<User>> getUserById(@RequestParam Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<>("User fetched successfully", user));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<User>> updateUser(@RequestParam Long id, @RequestBody UserDto userDto) {
        User updatedUser = userService.updateUsers(id, userDto);
        return ResponseEntity.ok(new ApiResponse<>("User updated successfully", updatedUser));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>("User deleted successfully", null));
    }
}
