package com.restaurant.booking.service.integration.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.restaurant.booking.dto.ReservationDTO;
import com.restaurant.booking.service.integration.CrmIntegrationService;

@Service
public class CrmIntegrationServiceImpl implements CrmIntegrationService {

   private static final Logger logger = LoggerFactory.getLogger(CrmIntegrationServiceImpl.class);

    @Override
    public void sendReservationToCrm(ReservationDTO reservationDTO) {
        logger.info("Sending reservation to CRM: {}", reservationDTO);
    }

}
