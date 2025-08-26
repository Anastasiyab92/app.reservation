package com.restaurant.booking.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {

    private Long reservationId;
    private String customerName;
    private String customerEmail;
    private String customerPhoneNumber;
    private int tableNumber;
    private LocalDateTime reservationDateTime;
    private String status;
}
