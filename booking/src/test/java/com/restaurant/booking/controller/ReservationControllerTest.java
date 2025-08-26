package com.restaurant.booking.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import com.restaurant.booking.dto.ReservationDTO;
import com.restaurant.booking.model.Reservation;
import com.restaurant.booking.model.Status;
import com.restaurant.booking.model.Table;
import com.restaurant.booking.model.User;
import com.restaurant.booking.repository.ReservationRepository;
import com.restaurant.booking.repository.TableRepository;
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
class ReservationControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    public void setUp() {
        // Clear existing data
        reservationRepository.deleteAll();
        tableRepository.deleteAll();
        userRepository.deleteAll();
        
        // Create test user
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
        userRepository.save(user);

        // Create test table
        Table table = new Table();
        table.setNumber(1);
        table.setCapacity(4);
        tableRepository.save(table);
    }

    @Test
    void testAvailableTablesEndpoint() {
        String url = "/api/reservations/available?date=2025-08-25&time=12:00&numberOfGuests=4";
        ResponseEntity<Table[]> response = restTemplate.getForEntity(url, Table[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Table[] tables = response.getBody();
        assertNotNull(tables);
        assertTrue(tables.length > 0);
    }

    @Test
    void testCreateReservationEndpoint() {
        User user = userRepository.findAll().get(0);
        Table table = tableRepository.findAll().get(0);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTable(table);
        reservation.setDate(LocalDate.of(2025, 8, 15));
        reservation.setTime(LocalTime.of(12, 0));
        reservation.setNumberOfGuests(4);
        reservation.setStatus(Status.BOOKED);

        ResponseEntity<ReservationDTO> response = restTemplate.postForEntity("/api/reservations", reservation, ReservationDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ReservationDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertNotNull(responseBody.getReservationId());
    }
}