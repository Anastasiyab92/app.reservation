package com.restaurant.booking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.restaurant.booking.dto.ReservationDTO;
import com.restaurant.booking.model.Reservation;
import com.restaurant.booking.model.Table;
import com.restaurant.booking.repository.ReservationRepository;
import com.restaurant.booking.repository.TableRepository;
import com.restaurant.booking.service.integration.CrmIntegrationService;
import com.restaurant.booking.service.integration.GastroIntegrationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        log.info("Checking available tables for date: {}, time: {}, guests: {}", date, time, numberOfGuests);
        
        List<Reservation> reservations = reservationRepository.findByDateAndTime(date, time);
        List<Table> allTables = tableRepository.findAll();

        Set<Long> reservedTableIds = reservations.stream()
            .map(reservation -> reservation.getTable().getId())
            .collect(Collectors.toSet());

        List<Table> availableTables = allTables.stream()
            .filter(table -> !reservedTableIds.contains(table.getId()) && table.getCapacity() >= numberOfGuests)
            .collect(Collectors.toList());
            
        log.info("Found {} available tables out of {} total tables", availableTables.size(), allTables.size());
        return availableTables;
    }

    public Reservation createReservation(Reservation reservation) {
        log.info("Creating reservation for user: {}, table: {}, date: {}, time: {}", 
            reservation.getUser().getName(), 
            reservation.getTable().getNumber(),
            reservation.getDate(),
            reservation.getTime());
            
        Reservation savedReservation = reservationRepository.save(reservation);
        ReservationDTO reservationDTO = convertToDTO(savedReservation);
        
        try {
            crmIntegrationService.sendReservationToCrm(reservationDTO);
            gastroIntegrationService.sendReservationToGastro(reservationDTO);
            log.info("Reservation {} created successfully and sent to external systems", savedReservation.getId());
        } catch (Exception e) {
            log.error("Failed to send reservation to external systems: {}", e.getMessage(), e);
            // Continue with the reservation creation even if external systems fail
        }
        
        return savedReservation;
    }

    public List<Reservation> getAllReservations() {
        log.debug("Retrieving all reservations");
        return reservationRepository.findAll();
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
