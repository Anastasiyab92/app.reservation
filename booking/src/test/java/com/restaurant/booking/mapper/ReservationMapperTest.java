package com.restaurant.booking.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.restaurant.booking.dto.ReservationDTO;
import com.restaurant.booking.model.Reservation;
import com.restaurant.booking.model.Status;
import com.restaurant.booking.model.Table;
import com.restaurant.booking.model.User;

class ReservationMapperTest {

    private ReservationMapper reservationMapper;

    @BeforeEach
    void setUp() {
        reservationMapper = new ReservationMapper();
    }

    @Test
    void toDto_ValidReservation_ReturnsCorrectDTO() {
        // Given
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPhoneNumber("1234567890");

        Table table = new Table();
        table.setNumber(5);

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setTable(table);
        reservation.setDate(LocalDate.of(2024, 12, 25));
        reservation.setTime(LocalTime.of(14, 30));
        reservation.setStatus(Status.BOOKED);

        // When
        ReservationDTO result = reservationMapper.toDto(reservation);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getReservationId());
        assertEquals("John Doe", result.getCustomerName());
        assertEquals("john@example.com", result.getCustomerEmail());
        assertEquals("1234567890", result.getCustomerPhoneNumber());
        assertEquals(5, result.getTableNumber());
        assertEquals(LocalDateTime.of(2024, 12, 25, 14, 30), result.getReservationDateTime());
        assertEquals("BOOKED", result.getStatus());
    }

    @Test
    void toDto_NullReservation_ReturnsNull() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            reservationMapper.toDto(null);
        });
    }

    @Test
    void toDto_ReservationWithNullFields_HandlesGracefully() {
        // Given
        User user = new User();
        // name, email, phoneNumber are null

        Table table = new Table();
        // number is null

        Reservation reservation = new Reservation();
        reservation.setId(2L);
        reservation.setUser(user);
        reservation.setTable(table);
        reservation.setDate(LocalDate.of(2024, 1, 1));
        reservation.setTime(LocalTime.of(12, 0));
        reservation.setStatus(Status.CANCELLED);

        // When
        ReservationDTO result = reservationMapper.toDto(reservation);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getReservationId());
        assertNull(result.getCustomerName());
        assertNull(result.getCustomerEmail());
        assertNull(result.getCustomerPhoneNumber());
        assertEquals(0, result.getTableNumber()); // int defaults to 0
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), result.getReservationDateTime());
        assertEquals("CANCELLED", result.getStatus());
    }

    @Test
    void toEntity_ValidDTO_ReturnsCorrectEntity() {
        // Given
        ReservationDTO reservationDTO = new ReservationDTO(
            1L, "Jane Doe", "jane@example.com", "0987654321", 
            3, LocalDateTime.of(2024, 6, 15, 18, 0), "AVAILABLE"
        );

        // When
        Reservation result = reservationMapper.toEntity(reservationDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(LocalDate.of(2024, 6, 15), result.getDate());
        assertEquals(LocalTime.of(18, 0), result.getTime());
        assertEquals(Status.AVAILABLE, result.getStatus());
    }

    @Test
    void toEntity_NullDTO_ReturnsNull() {
        // When
        Reservation result = reservationMapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    void toEntity_InvalidStatus_DefaultsToBooked() {
        // Given
        ReservationDTO reservationDTO = new ReservationDTO(
            1L, "Test User", "test@example.com", "1234567890", 
            1, LocalDateTime.of(2024, 1, 1, 12, 0), "INVALID_STATUS"
        );

        // When
        Reservation result = reservationMapper.toEntity(reservationDTO);

        // Then
        assertNotNull(result);
        assertEquals(Status.BOOKED, result.getStatus());
    }

    @Test
    void toEntity_ValidStatus_StatusMappedCorrectly() {
        // Given
        ReservationDTO reservationDTO = new ReservationDTO(
            1L, "Test User", "test@example.com", "1234567890", 
            1, LocalDateTime.of(2024, 1, 1, 12, 0), "CANCELLED"
        );

        // When
        Reservation result = reservationMapper.toEntity(reservationDTO);

        // Then
        assertNotNull(result);
        assertEquals(Status.CANCELLED, result.getStatus());
    }

    @Test
    void toEntity_DateTimeHandling_ConvertsCorrectly() {
        // Given
        LocalDateTime testDateTime = LocalDateTime.of(2024, 3, 15, 19, 45);
        ReservationDTO reservationDTO = new ReservationDTO(
            1L, "Test User", "test@example.com", "1234567890", 
            1, testDateTime, "BOOKED"
        );

        // When
        Reservation result = reservationMapper.toEntity(reservationDTO);

        // Then
        assertNotNull(result);
        assertEquals(LocalDate.of(2024, 3, 15), result.getDate());
        assertEquals(LocalTime.of(19, 45), result.getTime());
    }

    @Test
    void toEntity_NullDateTime_HandlesGracefully() {
        // Given
        ReservationDTO reservationDTO = new ReservationDTO(
            1L, "Test User", "test@example.com", "1234567890", 
            1, null, "BOOKED"
        );

        // When
        Reservation result = reservationMapper.toEntity(reservationDTO);

        // Then
        assertNotNull(result);
        assertNull(result.getDate());
        assertNull(result.getTime());
    }

    @Test
    void toEntity_NullStatus_HandlesGracefully() {
        // Given
        ReservationDTO reservationDTO = new ReservationDTO(
            1L, "Test User", "test@example.com", "1234567890", 
            1, LocalDateTime.of(2024, 1, 1, 12, 0), null
        );

        // When
        Reservation result = reservationMapper.toEntity(reservationDTO);

        // Then
        assertNotNull(result);
        assertNull(result.getStatus()); // Status remains null when null is passed
    }

    @Test
    void toDto_AllStatusTypes_StatusMappedCorrectly() {
        // Given
        User user = new User();
        user.setName("Status Test");
        user.setEmail("status@test.com");
        user.setPhoneNumber("1111111111");

        Table table = new Table();
        table.setNumber(1);

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setTable(table);
        reservation.setDate(LocalDate.of(2024, 1, 1));
        reservation.setTime(LocalTime.of(12, 0));

        // Test each status
        Status[] statuses = {Status.BOOKED, Status.AVAILABLE, Status.CANCELLED, Status.COMPLETED};
        String[] expectedStatuses = {"BOOKED", "AVAILABLE", "CANCELLED", "COMPLETED"};

        for (int i = 0; i < statuses.length; i++) {
            reservation.setStatus(statuses[i]);
            
            // When
            ReservationDTO result = reservationMapper.toDto(reservation);
            
            // Then
            assertNotNull(result);
            assertEquals(expectedStatuses[i], result.getStatus());
        }
    }
}
