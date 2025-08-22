package com.restaurant.booking.integration;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.restaurant.booking.model.Reservation;
import com.restaurant.booking.model.Status;
import com.restaurant.booking.model.Table;
import com.restaurant.booking.model.User;
import com.restaurant.booking.repository.ReservationRepository;
import com.restaurant.booking.repository.TableRepository;
import com.restaurant.booking.repository.UserRepository;

@SpringBootTest
public class ReservationIntegrationTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateAndRetrieveReservation(){
        User user = new User();
        user.setName("John Doe");
        user.setEmail("test@test.com");
        user.setPhoneNumber("1234567890");
        user = userRepository.save(user);

        Table table = new Table();
        table.setNumber(1);
        table.setCapacity(4);
        table = tableRepository.save(table);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTable(table);
        reservation.setDate(LocalDate.of(2025, 8, 15));
        reservation.setTime(LocalTime.of(18, 0));
        reservation.setNumberOfGuests(2);
        reservation.setStatus(Status.BOOKED);
        reservation = reservationRepository.save(reservation);

        assertNotNull(reservation.getId());
        assertEquals(Status.BOOKED, reservation.getStatus());
        assertEquals("John Doe", reservation.getUser().getName());
        assertEquals(1, reservation.getTable().getNumber());
    }
}
