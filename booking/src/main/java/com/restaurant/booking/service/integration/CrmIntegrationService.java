package com.restaurant.booking.service.integration;

import com.restaurant.booking.dto.ReservationDTO;

public interface CrmIntegrationService {

    void sendReservationToCrm(ReservationDTO reservationDTO);

}
