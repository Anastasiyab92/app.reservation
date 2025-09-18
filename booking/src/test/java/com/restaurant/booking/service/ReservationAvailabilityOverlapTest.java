package com.restaurant.booking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.restaurant.booking.model.Reservation;
import com.restaurant.booking.model.Table;
import com.restaurant.booking.repository.ReservationRepository;
import com.restaurant.booking.repository.TableRepository;
import com.restaurant.booking.service.integration.CrmIntegrationService;
import com.restaurant.booking.service.integration.GastroIntegrationService;

public class ReservationAvailabilityOverlapTest {

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
    void getAvailableTablesShouldExcludeTablesWithExactTimeOverlap() {
        LocalDate date = LocalDate.of(2025, 8, 25);
        LocalTime time = LocalTime.of(19, 0);

        Table t1 = new Table(); t1.setId(1L); t1.setNumber(1); t1.setCapacity(4);
        Table t2 = new Table(); t2.setId(2L); t2.setNumber(2); t2.setCapacity(4);

        Reservation r = new Reservation();
        r.setTable(t1);
        r.setDate(date);
        r.setTime(time);

        when(reservationRepository.findByDateAndTime(date, time)).thenReturn(List.of(r));
        when(tableRepository.findAll()).thenReturn(List.of(t1, t2));

        List<Table> available = reservationService.getAvailableTables(date, time, 2);

        assertThat(available).extracting(Table::getId).doesNotContain(1L);
        assertThat(available).extracting(Table::getId).contains(2L);
    }
}


