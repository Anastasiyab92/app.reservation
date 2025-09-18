package com.restaurant.booking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.restaurant.booking.model.Table;
import com.restaurant.booking.repository.TableRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableService {
    
    private final TableRepository tableRepository;
    
    public List<Table> getAllTables() {
        log.info("Retrieving all tables");
        return tableRepository.findAll();
    }
    
    public Optional<Table> getTableById(Long id) {
        log.info("Retrieving table with id: {}", id);
        return tableRepository.findById(id);
    }
    
    public Optional<Table> getTableByNumber(int number) {
        log.info("Retrieving table with number: {}", number);
        return tableRepository.findByNumber(number);
    }
    
    public Table createTable(Table table) {
        log.info("Creating new table with number: {}", table.getNumber());
        validateTable(table);
        return tableRepository.save(table);
    }
    
    public Table updateTable(Long id, Table tableDetails) {
        log.info("Updating table with id: {}", id);
        Table table = tableRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Table not found with id: " + id));
        
        table.setNumber(tableDetails.getNumber());
        table.setCapacity(tableDetails.getCapacity());
        
        validateTable(table);
        return tableRepository.save(table);
    }
    
    public void deleteTable(Long id) {
        log.info("Deleting table with id: {}", id);
        if (!tableRepository.existsById(id)) {
            throw new IllegalArgumentException("Table not found with id: " + id);
        }
        tableRepository.deleteById(id);
    }
    
    public boolean tableExists(Long id) {
        return tableRepository.existsById(id);
    }
    
    public boolean tableExistsByNumber(int number) {
        return tableRepository.findByNumber(number).isPresent();
    }
    
    private void validateTable(Table table) {
        if (table.getNumber() <= 0) {
            throw new IllegalArgumentException("Table number must be positive");
        }
        if (table.getCapacity() <= 0) {
            throw new IllegalArgumentException("Table capacity must be positive");
        }
        if (table.getCapacity() > 10) {
            throw new IllegalArgumentException("Table capacity cannot exceed 10");
        }
    }
}
