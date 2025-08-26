package com.restaurant.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurant.booking.dto.ReservationDTO;
import com.restaurant.booking.model.Reservation;
import com.restaurant.booking.model.Status;
import com.restaurant.booking.model.Table;
import com.restaurant.booking.model.User;
import com.restaurant.booking.repository.ReservationRepository;
import com.restaurant.booking.repository.TableRepository;
import com.restaurant.booking.service.integration.CrmIntegrationService;
import com.restaurant.booking.service.integration.GastroIntegrationService;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {
    @InjectMocks
    private ReservationService reservationService;
    @Mock
    private TableRepository tableRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private CrmIntegrationService crmIntegrationService;
    @Mock
    private GastroIntegrationService gastroIntegrationService;

    @BeforeEach
    public void setUp() {
        tableRepository = mock(TableRepository.class);
        reservationRepository = mock(ReservationRepository.class);
        crmIntegrationService = mock(CrmIntegrationService.class);
        gastroIntegrationService = mock(GastroIntegrationService.class);
        reservationService = new ReservationService(tableRepository, reservationRepository, crmIntegrationService, gastroIntegrationService);   
    }

    @Test
    void testGetAvailableTables(){
        LocalDate date = LocalDate.of(2025, 8, 15);
        LocalTime time = LocalTime.of(12, 0);

        Table table1 = new Table();
        table1.setId(1l);
        table1.setCapacity(4);
        Table table2 = new Table();
        table2.setId(2L);
        table2.setCapacity(4);

        Reservation reservation = new Reservation();
        reservation.setTable(table1);
        reservation.setDate(date);
        reservation.setTime(time);
        reservation.setNumberOfGuests(4);
        
        when(tableRepository.findAll()).thenReturn(Arrays.asList(table1, table2));
        when(reservationRepository.findByDateAndTime(date, time)).thenReturn(Arrays.asList(reservation));

        List<Table> availableTables = reservationService.getAvailableTables(date, time, 4);

        assertEquals(1, availableTables.size());
        assertEquals(2l, availableTables.get(0).getId());
        }

        @Test
        void testCreateReservation(){
            User user = new User();
            user.setId(1L);
            user.setName("Alex Smith");
            user.setEmail("alex@example.com");
            user.setPhoneNumber("1234567890");

            Table table = new Table();
            table.setId(1L);
            table.setNumber(1);
            table.setCapacity(4);

            Reservation reservation = new Reservation();
            reservation.setId(1L);
            reservation.setUser(user);
            reservation.setTable(table);
            reservation.setDate(LocalDate.of(2025, 8, 15));
            reservation.setTime(LocalTime.of(12, 0));
            reservation.setNumberOfGuests(4);
            reservation.setStatus(Status.BOOKED);

            when(reservationRepository.save(reservation)).thenReturn(reservation);

            Reservation createdReservation = reservationService.createReservation(reservation);

            assertNotNull(createdReservation);
            assertEquals(1L, createdReservation.getId());
            assertEquals(4, createdReservation.getNumberOfGuests());
            verify(reservationRepository, times(1)).save(reservation);
        }
        @Test
        void testCreateReservationTriggersIntegration(){
            User user = new User();
            user.setName("Alex Smith");
            user.setEmail("alex@example.com");
            user.setPhoneNumber("1234567890");

            Table table = new Table();
            table.setNumber(1);
            table.setCapacity(4);

            Reservation reservation = new Reservation();
            reservation.setId(1L);
            reservation.setUser(user);
            reservation.setTable(table);
            reservation.setDate(LocalDate.of(2025, 8, 15));
            reservation.setTime(LocalTime.of(12, 0));
            reservation.setNumberOfGuests(4);
            reservation.setStatus(Status.BOOKED);

            when(reservationRepository.save(reservation)).thenReturn(reservation);

            Reservation result = reservationService.createReservation(reservation);
            assertEquals(1L, result.getId());
            verify(crmIntegrationService, times(1)).sendReservationToCrm(any(ReservationDTO.class));
            verify(gastroIntegrationService, times(1)).sendReservationToGastro(any(ReservationDTO.class));
        }
}
