package com.restaurant.booking.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.restaurant.booking.dto.ReservationDTO;
import com.restaurant.booking.model.Reservation;
import com.restaurant.booking.model.Status;

@Component
public class ReservationMapper {

    public ReservationDTO toDto(Reservation reservation) {
        return new ReservationDTO(
            reservation.getId(),
            reservation.getUser().getName(),
            reservation.getUser().getEmail(),
            reservation.getUser().getPhoneNumber(),
            reservation.getTable().getNumber(),
            LocalDateTime.of(reservation.getDate(), reservation.getTime()),
            reservation.getStatus().name()
        );
    }
    
    public Reservation toEntity(ReservationDTO reservationDto) {
        if (reservationDto == null) {
            return null;
        }
        
        Reservation reservation = new Reservation();
        reservation.setId(reservationDto.getReservationId());
        
        // Note: User and Table would need to be set separately as they require repository lookups
        // This method is mainly for creating new reservations from DTOs
        
        if (reservationDto.getReservationDateTime() != null) {
            reservation.setDate(reservationDto.getReservationDateTime().toLocalDate());
            reservation.setTime(reservationDto.getReservationDateTime().toLocalTime());
        }
        
        // Status would need to be parsed from String to enum
        if (reservationDto.getStatus() != null) {
            try {
                reservation.setStatus(Status.valueOf(reservationDto.getStatus()));
            } catch (IllegalArgumentException e) {
                // Default to BOOKED if status is invalid
                reservation.setStatus(Status.BOOKED);
            }
        }
        
        return reservation;
    }

}
