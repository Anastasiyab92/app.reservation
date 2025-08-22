package com.restaurant.booking.integration.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReservationDTO {

    private Long reservationId;
    private String customerName;
    private String customerEmail;
    private String customerPhoneNumber;
    private int tableNumber;
    private LocalDateTime reservationDateTime;
    private String status;

    ReservationDTO(){}

    public ReservationDTO(Long reservationId, String customerName, String customerEmail, String customerPhoneNumber, int tableNumber, LocalDateTime reservationDateTime, String status) {
        this.reservationId = reservationId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhoneNumber = customerPhoneNumber;
        this.tableNumber = tableNumber;
        this.reservationDateTime = reservationDateTime;
        this.status = status;
    }
}
