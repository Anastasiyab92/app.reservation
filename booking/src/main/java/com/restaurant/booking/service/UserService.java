package com.restaurant.booking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.restaurant.booking.model.User;
import com.restaurant.booking.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User createUser(User user) {
        log.info("Creating new user with email: {}", user.getEmail());
        
        // Check if user with this email already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            log.warn("User with email {} already exists", user.getEmail());
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }
        
        // Validate user data
        validateUser(user);
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());
        return savedUser;
    }
    
    public User getUserById(Long id) {
        log.debug("Retrieving user by ID: {}", id);
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }
    
    public User getUserByEmail(String email) {
        log.debug("Retrieving user by email: {}", email);
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }
    
    public List<User> getAllUsers() {
        log.debug("Retrieving all users");
        return userRepository.findAll();
    }
    
    public User updateUser(Long id, User userDetails) {
        log.info("Updating user with ID: {}", id);
        
        User existingUser = getUserById(id);
        
        // Check if email is being changed and if it conflicts with another user
        if (!existingUser.getEmail().equals(userDetails.getEmail())) {
            Optional<User> userWithEmail = userRepository.findByEmail(userDetails.getEmail());
            if (userWithEmail.isPresent() && !userWithEmail.get().getId().equals(id)) {
                log.warn("Email {} is already taken by another user", userDetails.getEmail());
                throw new IllegalArgumentException("Email " + userDetails.getEmail() + " is already taken");
            }
        }
        
        // Update fields
        existingUser.setName(userDetails.getName());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setPhoneNumber(userDetails.getPhoneNumber());
        
        // Validate updated user
        validateUser(existingUser);
        
        User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully with ID: {}", updatedUser.getId());
        return updatedUser;
    }
    
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        
        if (!userRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent user with ID: {}", id);
            throw new IllegalArgumentException("User not found with ID: " + id);
        }
        
        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }
    
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }
    
    public boolean userExistsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    
    private void validateUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("User email cannot be empty");
        }
        
        // Basic email validation
        if (!user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty()) {
            // Basic phone number validation (at least 10 digits)
            String phoneDigits = user.getPhoneNumber().replaceAll("\\D", "");
            if (phoneDigits.length() < 10) {
                throw new IllegalArgumentException("Phone number must contain at least 10 digits");
            }
        }
    }
}
