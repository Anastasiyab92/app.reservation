package com.restaurant.booking.service;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.restaurant.booking.dto.ReservationDTO;
import com.restaurant.booking.model.Reservation;
import com.restaurant.booking.model.Status;
import com.restaurant.booking.model.Table;
import com.restaurant.booking.model.User;
import com.restaurant.booking.repository.ReservationRepository;
import com.restaurant.booking.repository.TableRepository;
import com.restaurant.booking.service.integration.CrmIntegrationService;
import com.restaurant.booking.service.integration.GastroIntegrationService;

public class ReservationServiceIntegrationMockTest {

    @Mock
    private TableRepository tableRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private CrmIntegrationService crmIntegrationService;
    @Mock
    private GastroIntegrationService gastroIntegrationService;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        reservationService = new ReservationService(tableRepository, reservationRepository, crmIntegrationService, gastroIntegrationService);
    }

    @Test
    void createReservationShouldContinueWhenIntegrationFails() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        user.setPhoneNumber("123");

        Table table = new Table();
        table.setId(1L);
        table.setNumber(1);
        table.setCapacity(4);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTable(table);
        reservation.setDate(LocalDate.now());
        reservation.setTime(LocalTime.NOON);
        reservation.setNumberOfGuests(2);
        reservation.setStatus(Status.BOOKED);

        when(reservationRepository.save(reservation)).thenReturn(reservation);
        doThrow(new RuntimeException("CRM down")).when(crmIntegrationService).sendReservationToCrm(org.mockito.ArgumentMatchers.any(ReservationDTO.class));

        reservationService.createReservation(reservation);

        verify(reservationRepository).save(reservation);
        verify(crmIntegrationService).sendReservationToCrm(org.mockito.ArgumentMatchers.any(ReservationDTO.class));
        // On CRM failure, service logs and continues without propagating, but does not reach Gastro call
    }
}


