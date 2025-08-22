package com.restaurant.booking.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import com.restaurant.booking.model.User;
import com.restaurant.booking.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=false",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPhoneNumber("1234567890");

        ResponseEntity<User> response = restTemplate.postForEntity("/api/users/register", user, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        User responseBody = response.getBody();
        assertNotNull(responseBody);
        assertNotNull(responseBody.getId());
        assertEquals(user.getName(), responseBody.getName());
        assertEquals(user.getEmail(), responseBody.getEmail());
    }

    @Test
    void testRegisterUserDuplicateEmail() {
        // Create first user
        User user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("john@example.com");
        user1.setPhoneNumber("1234567890");
        userRepository.save(user1);

        // Try to create second user with same email
        User user2 = new User();
        user2.setName("Jane Doe");
        user2.setEmail("john@example.com");
        user2.setPhoneNumber("0987654321");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/users/register", user2, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("User with email john@example.com already exists"));
    }

    @Test
    void testGetUserById() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPhoneNumber("1234567890");
        User savedUser = userRepository.save(user);

        ResponseEntity<User> response = restTemplate.getForEntity("/api/users/" + savedUser.getId(), User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        User responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(savedUser.getId(), responseBody.getId());
        assertEquals(savedUser.getName(), responseBody.getName());
    }

    @Test
    void testGetUserByIdNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/users/999", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("User not found with ID: 999"));
    }

    @Test
    void testGetUserByEmail() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPhoneNumber("1234567890");
        userRepository.save(user);

        ResponseEntity<User> response = restTemplate.getForEntity("/api/users/email/john@example.com", User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        User responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(user.getEmail(), responseBody.getEmail());
        assertEquals(user.getName(), responseBody.getName());
    }

    @Test
    void testGetUserByEmailNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/users/email/nonexistent@example.com", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("User not found with email: nonexistent@example.com"));
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("john@example.com");
        user1.setPhoneNumber("1234567890");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("Jane Doe");
        user2.setEmail("jane@example.com");
        user2.setPhoneNumber("0987654321");
        userRepository.save(user2);

        ResponseEntity<User[]> response = restTemplate.getForEntity("/api/users", User[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        User[] responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.length);
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPhoneNumber("1234567890");
        User savedUser = userRepository.save(user);

        User updatedUser = new User();
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john.updated@example.com");
        updatedUser.setPhoneNumber("1111111111");

        restTemplate.put("/api/users/" + savedUser.getId(), updatedUser);

        User retrievedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(retrievedUser);
        assertEquals(updatedUser.getName(), retrievedUser.getName());
        assertEquals(updatedUser.getEmail(), retrievedUser.getEmail());
        assertEquals(updatedUser.getPhoneNumber(), retrievedUser.getPhoneNumber());
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPhoneNumber("1234567890");
        User savedUser = userRepository.save(user);

        restTemplate.delete("/api/users/" + savedUser.getId());

        assertFalse(userRepository.existsById(savedUser.getId()));
    }

    @Test
    void testCheckUserExists() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPhoneNumber("1234567890");
        User savedUser = userRepository.save(user);

        ResponseEntity<Boolean> response = restTemplate.getForEntity("/api/users/" + savedUser.getId() + "/exists", Boolean.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Boolean responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody);
    }

    @Test
    void testCheckUserExistsByEmail() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPhoneNumber("1234567890");
        userRepository.save(user);

        ResponseEntity<Boolean> response = restTemplate.getForEntity("/api/users/email/john@example.com/exists", Boolean.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Boolean responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody);
    }

    @Test
    void testCheckUserExistsByEmailNotFound() {
        ResponseEntity<Boolean> response = restTemplate.getForEntity("/api/users/email/nonexistent@example.com/exists", Boolean.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Boolean responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody);
    }
}
