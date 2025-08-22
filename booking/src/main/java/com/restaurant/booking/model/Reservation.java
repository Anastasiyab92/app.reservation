package com.restaurant.booking.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Table table;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime time;

    @NotNull
    @Min(1)
    @Max(10)
    private int numberOfGuests;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Override
    public String toString() {
        return "Reservation(id=" + id + ", date=" + date + ", time=" + time + ", numberOfGuests=" + numberOfGuests + ", status=" + status + ")";
    }
}
