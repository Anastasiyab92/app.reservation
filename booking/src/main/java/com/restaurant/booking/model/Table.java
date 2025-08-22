package com.restaurant.booking.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Table {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int number;
    private int capacity;

    @OneToMany(mappedBy = "table")
    @JsonIgnore
    private List<Reservation> reservations;

    @Override
    public String toString() {
        return "Table(id=" + id + ", number=" + number + ", capacity=" + capacity + ")";
    }
}
