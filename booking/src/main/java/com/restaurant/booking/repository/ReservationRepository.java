package com.restaurant.booking.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurant.booking.model.Reservation;
import com.restaurant.booking.model.Status;
import com.restaurant.booking.model.User;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

List<Reservation> findByDateAndTime(LocalDate date, LocalTime time);
List<Reservation> findByUserId(User user);
List<Reservation> findByStatus(Status status);
}
