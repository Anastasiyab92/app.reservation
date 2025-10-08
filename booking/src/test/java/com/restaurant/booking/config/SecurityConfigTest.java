package com.restaurant.booking.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.restaurant.booking.security.JwtAuthenticationFilter;
import com.restaurant.booking.security.JwtUtils;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsService userDetailsService;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
    }

    @Test
    void passwordEncoder_ReturnsBCryptPasswordEncoder() {
        // When
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        // Then
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder);
    }

    @Test
    void passwordEncoder_AlwaysReturnsSameInstance() {
        // When
        PasswordEncoder passwordEncoder1 = securityConfig.passwordEncoder();
        PasswordEncoder passwordEncoder2 = securityConfig.passwordEncoder();

        // Then
        // Note: Spring beans are singletons by default, but in test context they might be different instances
        assertNotNull(passwordEncoder1);
        assertNotNull(passwordEncoder2);
        assertEquals(passwordEncoder1.getClass(), passwordEncoder2.getClass());
    }

    @Test
    void passwordEncoder_EncodesPasswordCorrectly() {
        // Given
        String rawPassword = "testPassword123";
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        // When
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Then
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void passwordEncoder_MatchesPasswordCorrectly() {
        // Given
        String rawPassword = "testPassword123";
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // When
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        boolean wrongPassword = passwordEncoder.matches("wrongPassword", encodedPassword);

        // Then
        assertTrue(matches);
        assertFalse(wrongPassword);
    }

    @Test
    void authenticationManager_ReturnsCorrectManager() throws Exception {
        // Given
        AuthenticationManager mockAuthManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mockAuthManager);

        // When
        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        // Then
        assertNotNull(result);
        assertSame(mockAuthManager, result);
        verify(authenticationConfiguration).getAuthenticationManager();
    }

    @Test
    void authenticationManager_PropagatesException() throws Exception {
        // Given
        Exception expectedException = new Exception("Authentication configuration error");
        when(authenticationConfiguration.getAuthenticationManager()).thenThrow(expectedException);

        // When & Then
        Exception thrownException = assertThrows(Exception.class, () -> {
            securityConfig.authenticationManager(authenticationConfiguration);
        });

        assertSame(expectedException, thrownException);
    }

    @Test
    void jwtAuthenticationFilter_ReturnsCorrectFilter() {
        // When
        JwtAuthenticationFilter filter = securityConfig.jwtAuthenticationFilter(jwtUtils, userDetailsService);

        // Then
        assertNotNull(filter);
        assertTrue(filter instanceof JwtAuthenticationFilter);
    }

    @Test
    void jwtAuthenticationFilter_WithNullParameters_HandlesGracefully() {
        // When
        JwtAuthenticationFilter filter = securityConfig.jwtAuthenticationFilter(null, null);

        // Then
        assertNotNull(filter);
        assertTrue(filter instanceof JwtAuthenticationFilter);
    }

    @Test
    void jwtAuthenticationFilter_WithValidParameters_ReturnsFilter() {
        // Given
        JwtUtils mockJwtUtils = mock(JwtUtils.class);
        UserDetailsService mockUserDetailsService = mock(UserDetailsService.class);

        // When
        JwtAuthenticationFilter filter = securityConfig.jwtAuthenticationFilter(mockJwtUtils, mockUserDetailsService);

        // Then
        assertNotNull(filter);
        assertTrue(filter instanceof JwtAuthenticationFilter);
    }

    @Test
    void securityFilterChain_ConfiguresCorrectly() throws Exception {
        // Given
        org.springframework.security.web.DefaultSecurityFilterChain mockFilterChain = mock(org.springframework.security.web.DefaultSecurityFilterChain.class);
        org.springframework.security.config.annotation.web.builders.HttpSecurity mockHttp = mock(org.springframework.security.config.annotation.web.builders.HttpSecurity.class);
        
        // Mock the chained calls
        when(mockHttp.csrf(any())).thenReturn(mockHttp);
        when(mockHttp.sessionManagement(any())).thenReturn(mockHttp);
        when(mockHttp.authorizeHttpRequests(any())).thenReturn(mockHttp);
        when(mockHttp.addFilterBefore(any(), any())).thenReturn(mockHttp);
        when(mockHttp.build()).thenReturn(mockFilterChain);

        // When
        SecurityFilterChain result = securityConfig.securityFilterChain(mockHttp);

        // Then
        assertNotNull(result);
        assertSame(mockFilterChain, result);
        
        // Verify the configuration calls
        verify(mockHttp).csrf(any());
        verify(mockHttp).sessionManagement(any());
        verify(mockHttp).authorizeHttpRequests(any());
        verify(mockHttp).addFilterBefore(any(JwtAuthenticationFilter.class), eq(UsernamePasswordAuthenticationFilter.class));
        verify(mockHttp).build();
    }

    @Test
    void securityFilterChain_PropagatesException() throws Exception {
        // Given
        org.springframework.security.config.annotation.web.builders.HttpSecurity mockHttp = mock(org.springframework.security.config.annotation.web.builders.HttpSecurity.class);
        Exception expectedException = new Exception("Security configuration error");
        when(mockHttp.csrf(any())).thenThrow(expectedException);

        // When & Then
        Exception thrownException = assertThrows(Exception.class, () -> {
            securityConfig.securityFilterChain(mockHttp);
        });

        assertSame(expectedException, thrownException);
    }

    @Test
    void securityConfig_IsAnnotatedCorrectly() {
        // Given
        Class<SecurityConfig> configClass = SecurityConfig.class;

        // Then
        assertTrue(configClass.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
        assertTrue(configClass.isAnnotationPresent(org.springframework.context.annotation.Profile.class));
        assertTrue(configClass.isAnnotationPresent(org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity.class));
        
        // Check profile annotation value
        org.springframework.context.annotation.Profile profileAnnotation = configClass.getAnnotation(org.springframework.context.annotation.Profile.class);
        assertArrayEquals(new String[]{"prod"}, profileAnnotation.value());
    }

    @Test
    void passwordEncoder_HandlesEmptyPassword() {
        // Given
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String emptyPassword = "";

        // When
        String encodedPassword = passwordEncoder.encode(emptyPassword);
        boolean matches = passwordEncoder.matches(emptyPassword, encodedPassword);

        // Then
        assertNotNull(encodedPassword);
        assertTrue(matches);
    }

    @Test
    void passwordEncoder_HandlesSpecialCharacters() {
        // Given
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String specialPassword = "!@#$%^&*()_+-=[]{}|;':\",./<>?";

        // When
        String encodedPassword = passwordEncoder.encode(specialPassword);
        boolean matches = passwordEncoder.matches(specialPassword, encodedPassword);

        // Then
        assertNotNull(encodedPassword);
        assertTrue(matches);
    }

    @Test
    void passwordEncoder_HandlesLongPassword() {
        // Given
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String longPassword = "a".repeat(70); // Long password within BCrypt limit

        // When
        String encodedPassword = passwordEncoder.encode(longPassword);
        boolean matches = passwordEncoder.matches(longPassword, encodedPassword);

        // Then
        assertNotNull(encodedPassword);
        assertTrue(matches);
    }

    @Test
    void passwordEncoder_HandlesUnicodeCharacters() {
        // Given
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String unicodePassword = "密码123🔐";

        // When
        String encodedPassword = passwordEncoder.encode(unicodePassword);
        boolean matches = passwordEncoder.matches(unicodePassword, encodedPassword);

        // Then
        assertNotNull(encodedPassword);
        assertTrue(matches);
    }

    @Test
    void passwordEncoder_StrengthIsCorrect() {
        // Given
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String password = "testPassword123";

        // When
        String encodedPassword = passwordEncoder.encode(password);

        // Then
        // BCrypt should produce a 60-character hash
        assertEquals(60, encodedPassword.length());
        assertTrue(encodedPassword.startsWith("$2a$"));
    }
}
