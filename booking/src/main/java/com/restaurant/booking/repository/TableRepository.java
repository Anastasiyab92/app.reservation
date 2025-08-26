package com.restaurant.booking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.restaurant.booking.model.Table;

@Repository
public interface TableRepository extends JpaRepository<Table, Long> {

    @NonNull
    List<Table> findAll();

    Optional<Table> findByNumber(int number);
    // Additional query methods can be defined here if needed

}
