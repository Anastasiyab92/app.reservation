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

import com.restaurant.booking.dto.ReservationDTO;
import com.restaurant.booking.mapper.ReservationMapper;
import com.restaurant.booking.model.Reservation;
import com.restaurant.booking.model.Table;
import com.restaurant.booking.service.ReservationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservation Management", description = "APIs for managing restaurant reservations")
@Validated
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    public ReservationController(ReservationService reservationService, ReservationMapper reservationMapper) {
        this.reservationService = reservationService;
        this.reservationMapper = reservationMapper;
    }

    @GetMapping("/available")
    @Operation(summary = "Check table availability", description = "Find available tables for a specific date, time, and number of guests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Available tables found",
            content = @Content(schema = @Schema(implementation = Table.class))),
        @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    public ResponseEntity<List<Table>> checkAvailability(
        @Parameter(description = "Reservation date (YYYY-MM-DD)") @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, 
        @Parameter(description = "Reservation time (HH:MM)") @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time, 
        @Parameter(description = "Number of guests (1-10)") @RequestParam @NotNull @Min(1) @Max(10) int numberOfGuests) {
        
        List<Table> availableTables = reservationService.getAvailableTables(date, time, numberOfGuests);
        return ResponseEntity.ok(availableTables);
    }

    @PostMapping
    @Operation(summary = "Create a reservation", description = "Creates a new reservation for a table")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation created successfully",
            content = @Content(schema = @Schema(implementation = ReservationDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid reservation data")
    })
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
