package com.restaurant.booking.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.restaurant.booking.dto.UserDTO;
import com.restaurant.booking.model.User;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void toDto_ValidUser_ReturnsCorrectDTO() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPhoneNumber("1234567890");

        // When
        UserDTO result = userMapper.toDto(user);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("1234567890", result.getPhoneNumber());
    }

    @Test
    void toDto_NullUser_ReturnsNull() {
        // When
        UserDTO result = userMapper.toDto(null);

        // Then
        assertNull(result);
    }

    @Test
    void toDto_UserWithNullFields_HandlesGracefully() {
        // Given
        User user = new User();
        user.setId(2L);
        // name, email, phoneNumber are null

        // When
        UserDTO result = userMapper.toDto(user);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertNull(result.getName());
        assertNull(result.getEmail());
        assertNull(result.getPhoneNumber());
    }

    @Test
    void toEntity_ValidDTO_ReturnsCorrectEntity() {
        // Given
        UserDTO userDTO = new UserDTO(1L, "Jane Doe", "jane@example.com", "0987654321");

        // When
        User result = userMapper.toEntity(userDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Jane Doe", result.getName());
        assertEquals("jane@example.com", result.getEmail());
        assertEquals("0987654321", result.getPhoneNumber());
    }

    @Test
    void toEntity_NullDTO_ReturnsNull() {
        // When
        User result = userMapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    void toEntity_DTOWithNullFields_HandlesGracefully() {
        // Given
        UserDTO userDTO = new UserDTO(2L, null, null, null);

        // When
        User result = userMapper.toEntity(userDTO);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertNull(result.getName());
        assertNull(result.getEmail());
        assertNull(result.getPhoneNumber());
    }

    @Test
    void toDto_AllFields_AllFieldsMappedCorrectly() {
        // Given
        User user = new User();
        user.setId(999L);
        user.setName("Test User");
        user.setEmail("test@test.com");
        user.setPhoneNumber("555-1234");

        // When
        UserDTO result = userMapper.toDto(user);

        // Then
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getPhoneNumber(), result.getPhoneNumber());
    }

    @Test
    void toEntity_AllFields_AllFieldsMappedCorrectly() {
        // Given
        UserDTO userDTO = new UserDTO(888L, "All Fields", "all@fields.com", "999-8888");

        // When
        User result = userMapper.toEntity(userDTO);

        // Then
        assertNotNull(result);
        assertEquals(userDTO.getId(), result.getId());
        assertEquals(userDTO.getName(), result.getName());
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertEquals(userDTO.getPhoneNumber(), result.getPhoneNumber());
    }
}


