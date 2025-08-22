package com.restaurant.booking.integration;

import com.restaurant.booking.integration.dto.ReservationDTO;

public interface GastroIntegrationService {

    void sendReservationToGastro(ReservationDTO reservationDTO);
}
