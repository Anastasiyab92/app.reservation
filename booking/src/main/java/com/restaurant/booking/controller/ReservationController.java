package com.restaurant.booking.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.booking.integration.dto.ReservationDTO;
import com.restaurant.booking.mapper.ReservationMapper;
import com.restaurant.booking.model.Reservation;
import com.restaurant.booking.model.Table;
import com.restaurant.booking.service.ReservationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    public ReservationController(ReservationService reservationService, ReservationMapper reservationMapper) {
        this.reservationService = reservationService;
        this.reservationMapper = reservationMapper;
    }

    @GetMapping("/available")
    public ResponseEntity<List<Table>> checkAvailability(
        @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, 
        @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time, 
        @RequestParam @NotNull @Min(1) @Max(20) int numberOfGuests) {
        
        List<Table> availableTables = reservationService.getAvailableTables(date, time, numberOfGuests);
        return ResponseEntity.ok(availableTables);
    }

    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@Valid @RequestBody Reservation reservation) {
        Reservation saved = reservationService.createReservation(reservation);
        ReservationDTO dto = reservationMapper.toDto(saved);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = reservationService.getAllReservations().stream()
            .map(reservationMapper::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(reservations);
    }
}
