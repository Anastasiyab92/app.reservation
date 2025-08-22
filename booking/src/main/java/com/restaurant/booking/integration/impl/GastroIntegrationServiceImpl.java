package com.restaurant.booking.integration.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restaurant.booking.integration.GastroIntegrationService;
import com.restaurant.booking.integration.dto.ReservationDTO;

public class GastroIntegrationServiceImpl implements GastroIntegrationService{

    private static final Logger logger = LoggerFactory.getLogger(GastroIntegrationServiceImpl.class);

    @Override
    public void sendReservationToGastro(ReservationDTO reservationDTO) {
        logger.info("Sending reservation to Gastro: {}", reservationDTO);
    }

}
