package com.restaurant.booking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurant.booking.model.User;
import com.restaurant.booking.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setPhoneNumber("1234567890");
    }

    @Test
    void testCreateUserSuccess() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User createdUser = userService.createUser(testUser);

        assertNotNull(createdUser);
        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getName(), createdUser.getName());
        assertEquals(testUser.getEmail(), createdUser.getEmail());
        verify(userRepository).findByEmail(testUser.getEmail());
        verify(userRepository).save(testUser);
    }

    @Test
    void testCreateUserEmailAlreadyExists() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.createUser(testUser));

        assertEquals("User with email " + testUser.getEmail() + " already exists", exception.getMessage());
        verify(userRepository).findByEmail(testUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUserInvalidEmail() {
        testUser.setEmail("invalid-email");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.createUser(testUser));

        assertEquals("Invalid email format", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUserEmptyName() {
        testUser.setName("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.createUser(testUser));

        assertEquals("User name cannot be empty", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserByIdSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User foundUser = userService.getUserById(1L);

        assertNotNull(foundUser);
        assertEquals(testUser.getId(), foundUser.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.getUserById(1L));

        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetUserByEmailSuccess() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        User foundUser = userService.getUserByEmail(testUser.getEmail());

        assertNotNull(foundUser);
        assertEquals(testUser.getEmail(), foundUser.getEmail());
        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    void testGetUserByEmailNotFound() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.getUserByEmail(testUser.getEmail()));

        assertEquals("User not found with email: " + testUser.getEmail(), exception.getMessage());
        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> foundUsers = userService.getAllUsers();

        assertNotNull(foundUsers);
        assertEquals(1, foundUsers.size());
        assertEquals(testUser, foundUsers.get(0));
        verify(userRepository).findAll();
    }

    @Test
    void testUpdateUserSuccess() {
        User updatedUser = new User();
        updatedUser.setName("Jane Doe");
        updatedUser.setEmail("jane@example.com");
        updatedUser.setPhoneNumber("0987654321");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail(updatedUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(1L, updatedUser);

        assertNotNull(result);
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(updatedUser.getEmail(), result.getEmail());
        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail(updatedUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUserEmailConflict() {
        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setEmail("jane@example.com");

        User updatedUser = new User();
        updatedUser.setName("Jane Doe");
        updatedUser.setEmail("jane@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail(updatedUser.getEmail())).thenReturn(Optional.of(existingUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.updateUser(1L, updatedUser));

        assertEquals("Email " + updatedUser.getEmail() + " is already taken", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail(updatedUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUserSuccess() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.deleteUser(1L));

        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(any(Long.class));
    }

    @Test
    void testUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean exists = userService.userExists(1L);

        assertTrue(exists);
        verify(userRepository).existsById(1L);
    }

    @Test
    void testUserExistsByEmail() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        boolean exists = userService.userExistsByEmail(testUser.getEmail());

        assertTrue(exists);
        verify(userRepository).findByEmail(testUser.getEmail());
    }
}
