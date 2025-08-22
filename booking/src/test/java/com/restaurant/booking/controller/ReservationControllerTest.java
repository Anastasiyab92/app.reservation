package com.restaurant.booking.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.restaurant.booking.model.Reservation;
import com.restaurant.booking.model.Status;
import com.restaurant.booking.model.Table;
import com.restaurant.booking.model.User;
import com.restaurant.booking.repository.ReservationRepository;
import com.restaurant.booking.repository.TableRepository;
import com.restaurant.booking.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ReservationControllerTest {

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
        reservationRepository.deleteAll();
        tableRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setName("Anna");
        user.setEmail("anna@example.com");
        user.setPhoneNumber("1234567890");
        userRepository.save(user);

        Table table = new Table();
        table.setNumber(1);
        table.setCapacity(4);
        tableRepository.save(table);

    }

    @Test
    void testAvailableTablesEndpoint() {
        String url = "/api/reservations/available?date=2025-08-15&time=12:00&numberOfGuests=2";
        ResponseEntity<Table[]> response = restTemplate.getForEntity(url, Table[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Table[] tables = response.getBody();
        assertNotNull(tables, "Tables should not be null");
        assertTrue(tables.length > 0, "At least one table should be available");
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
        reservation.setNumberOfGuests(2);
        reservation.setStatus(Status.BOOKED);

        ResponseEntity<Reservation> response = restTemplate.postForEntity("/api/reservations", reservation, Reservation.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Reservation createdReservation = response.getBody();
        assertNotNull(createdReservation, "Created reservation should not be null");
        assertNotNull(createdReservation.getId(), "Created reservation should have an ID");
    }

}