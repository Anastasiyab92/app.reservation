package com.restaurant.booking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.restaurant.booking.integration.CrmIntegrationService;
import com.restaurant.booking.integration.GastroIntegrationService;
import com.restaurant.booking.integration.dto.ReservationDTO;
import com.restaurant.booking.model.Reservation;
import com.restaurant.booking.model.Table;
import com.restaurant.booking.repository.ReservationRepository;
import com.restaurant.booking.repository.TableRepository;


@Service
public class ReservationService {
    
    private final TableRepository tableRepository;
    private final ReservationRepository reservationRepository;
    private final CrmIntegrationService crmIntegrationService;
    private final GastroIntegrationService gastroIntegrationService;

    public ReservationService(TableRepository tableRepository, ReservationRepository reservationRepository, CrmIntegrationService crmIntegrationService, GastroIntegrationService gastroIntegrationService) {
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
        this.crmIntegrationService = crmIntegrationService;
        this.gastroIntegrationService = gastroIntegrationService;
    }   

    public List<Table> getAvailableTables(LocalDate date, LocalTime time, int numberOfGuests) {
        List<Reservation> reservations = reservationRepository.findByDateAndTime(date, time);
        List<Table> allTables = tableRepository.findAll();

        Set<Long> reservedTableIds = reservations.stream()
            .map(reservation -> reservation.getTable().getId())
            .collect(Collectors.toSet());

            return allTables.stream()
            .filter(table -> !reservedTableIds.contains(table.getId()) && table.getCapacity() >= numberOfGuests)
            .collect(Collectors.toList());
    }

    public Reservation createReservation(Reservation reservation) {
        Reservation savedReservation = reservationRepository.save(reservation);
        ReservationDTO reservationDTO = convertToDTO(savedReservation);
        crmIntegrationService.sendReservationToCrm(reservationDTO);
        gastroIntegrationService.sendReservationToGastro(reservationDTO);
        return savedReservation;
        
    }

    private ReservationDTO convertToDTO(Reservation reservation) {
        return new ReservationDTO(
            reservation.getId(), 
            reservation.getUser().getName(), 
            reservation.getUser().getEmail(), 
            reservation.getUser().getPhoneNumber(), 
            reservation.getTable().getNumber(),
            LocalDateTime.of(reservation.getDate(), reservation.getTime()),
            reservation.getStatus().toString());
    }

}
