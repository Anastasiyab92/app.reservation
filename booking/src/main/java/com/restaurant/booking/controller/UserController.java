package com.restaurant.booking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.booking.model.User;
import com.restaurant.booking.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing restaurant users")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "User with this email already exists")
    })
    public ResponseEntity<User> registerUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> getUserById(@Parameter(description = "User ID") @PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieves a user by their email address")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> getUserByEmail(@Parameter(description = "User email address") @PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a list of all registered users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of users retrieved successfully",
            content = @Content(schema = @Schema(implementation = User.class)))
    })
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates an existing user's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> updateUser(@Parameter(description = "User ID") @PathVariable Long id, @Valid @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(@Parameter(description = "User ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if user exists", description = "Checks if a user exists by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User existence check completed",
            content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ResponseEntity<Boolean> checkUserExists(@Parameter(description = "User ID") @PathVariable Long id) {
        boolean exists = userService.userExists(id);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/email/{email}/exists")
    @Operation(summary = "Check if user exists by email", description = "Checks if a user exists by their email address")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User existence check completed",
            content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ResponseEntity<Boolean> checkUserExistsByEmail(@Parameter(description = "User email address") @PathVariable String email) {
        boolean exists = userService.userExistsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}
