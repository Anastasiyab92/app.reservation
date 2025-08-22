package com.restaurant.booking.integration.impl;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restaurant.booking.integration.CrmIntegrationService;
import com.restaurant.booking.integration.dto.ReservationDTO;

public class CrmIntegrationServiceImpl implements CrmIntegrationService {

   private static final Logger logger = LoggerFactory.getLogger(CrmIntegrationServiceImpl.class);

    @Override
    public void sendReservationToCrm(ReservationDTO reservationDTO) {
        logger.info("Sending reservation to CRM: {}", reservationDTO);
    }

}
