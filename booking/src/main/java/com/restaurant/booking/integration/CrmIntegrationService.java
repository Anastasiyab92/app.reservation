package com.restaurant.booking.integration;

import com.restaurant.booking.integration.dto.ReservationDTO;

public interface CrmIntegrationService {

    void sendReservationToCrm(ReservationDTO reservationDTO);

}
