package com.restaurant.booking.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.restaurant.booking.model.AppUser;
import com.restaurant.booking.model.Role;
import com.restaurant.booking.repository.AppUserRepository;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private AppUserRepository userRepository;

    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByUsername_ValidEmail_ReturnsUserDetails() {
        // Given
        String email = "test@example.com";
        AppUser appUser = new AppUser();
        appUser.setEmail(email);
        appUser.setPassword("encodedPassword");
        appUser.setRole(Role.USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(appUser));

        // When
        UserDetails result = userDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
        assertTrue(result.isEnabled());
        
        verify(userRepository).findByEmail(email);
    }

    @Test
    void loadUserByUsername_InvalidEmail_ThrowsUsernameNotFoundException() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void loadUserByUsername_UserWithAdminRole_IncludesCorrectAuthority() {
        // Given
        String email = "admin@example.com";
        AppUser appUser = new AppUser();
        appUser.setEmail(email);
        appUser.setPassword("adminPassword");
        appUser.setRole(Role.ADMIN);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(appUser));

        // When
        UserDetails result = userDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertTrue(result.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        assertFalse(result.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_UserWithAdminRole2_IncludesCorrectAuthority() {
        // Given
        String email = "admin2@example.com";
        AppUser appUser = new AppUser();
        appUser.setEmail(email);
        appUser.setPassword("adminPassword2");
        appUser.setRole(Role.ADMIN);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(appUser));

        // When
        UserDetails result = userDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertTrue(result.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_UserWithNullRole_HandlesGracefully() {
        // Given
        String email = "test@example.com";
        AppUser appUser = new AppUser();
        appUser.setEmail(email);
        appUser.setPassword("encodedPassword");
        appUser.setRole(null);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(appUser));

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });
    }

    @Test
    void loadUserByUsername_UserWithEmptyPassword_HandlesCorrectly() {
        // Given
        String email = "test@example.com";
        AppUser appUser = new AppUser();
        appUser.setEmail(email);
        appUser.setPassword("");
        appUser.setRole(Role.USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(appUser));

        // When
        UserDetails result = userDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertEquals("", result.getPassword());
    }

    @Test
    void loadUserByUsername_UserWithNullPassword_HandlesCorrectly() {
        // Given
        String email = "test@example.com";
        AppUser appUser = new AppUser();
        appUser.setEmail(email);
        appUser.setPassword(null);
        appUser.setRole(Role.USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(appUser));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });
    }

    @Test
    void loadUserByUsername_AllRoleTypes_AuthorityMappedCorrectly() {
        // Given
        Role[] roles = {Role.USER, Role.ADMIN};
        String[] expectedAuthorities = {"ROLE_USER", "ROLE_ADMIN"};

        for (int i = 0; i < roles.length; i++) {
            final int index = i; // Make final for lambda
            String email = "test" + i + "@example.com";
            AppUser appUser = new AppUser();
            appUser.setEmail(email);
            appUser.setPassword("password");
            appUser.setRole(roles[i]);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(appUser));

            // When
            UserDetails result = userDetailsService.loadUserByUsername(email);

            // Then
            assertNotNull(result);
            assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(expectedAuthorities[index])));
        }
    }

    @Test
    void loadUserByUsername_RepositoryThrowsException_PropagatesException() {
        // Given
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });
    }

    @Test
    void loadUserByUsername_EmailWithSpecialCharacters_HandlesCorrectly() {
        // Given
        String email = "test+user@example-domain.com";
        AppUser appUser = new AppUser();
        appUser.setEmail(email);
        appUser.setPassword("encodedPassword");
        appUser.setRole(Role.USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(appUser));

        // When
        UserDetails result = userDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getUsername());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void loadUserByUsername_EmptyEmail_HandlesCorrectly() {
        // Given
        String email = "";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail(email);
    }
}
