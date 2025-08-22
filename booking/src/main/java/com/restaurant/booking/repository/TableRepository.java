package com.restaurant.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.restaurant.booking.model.Table;

public interface TableRepository extends JpaRepository<Table, Long> {

    @NonNull
    List<Table> findAll();
    // Additional query methods can be defined here if needed

}
