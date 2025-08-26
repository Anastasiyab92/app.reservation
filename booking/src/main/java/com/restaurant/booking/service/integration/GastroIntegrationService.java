package com.restaurant.booking.service.integration;

import com.restaurant.booking.dto.ReservationDTO;

public interface GastroIntegrationService {

    void sendReservationToGastro(ReservationDTO reservationDTO);
}
