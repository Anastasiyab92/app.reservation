package com.restaurant.booking.service.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.restaurant.booking.integration.GastroIntegrationService;
import com.restaurant.booking.integration.dto.ReservationDTO;

@Service
public class GastroIntegrationServiceImpl implements GastroIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(GastroIntegrationServiceImpl.class);

    @Override
    public void sendReservationToGastro(ReservationDTO reservationDTO) {
        logger.info("Sending reservation to Gastro: {}", reservationDTO);
    }

}
