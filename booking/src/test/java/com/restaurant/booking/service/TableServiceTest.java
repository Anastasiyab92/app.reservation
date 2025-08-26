package com.restaurant.booking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurant.booking.model.Table;
import com.restaurant.booking.repository.TableRepository;

@ExtendWith(MockitoExtension.class)
class TableServiceTest {
    
    @Mock
    private TableRepository tableRepository;
    
    @InjectMocks
    private TableService tableService;
    
    private Table testTable;
    
    @BeforeEach
    void setUp() {
        testTable = new Table();
        testTable.setId(1L);
        testTable.setNumber(1);
        testTable.setCapacity(4);
    }
    
    @Test
    void testGetAllTables() {
        List<Table> expectedTables = Arrays.asList(testTable);
        when(tableRepository.findAll()).thenReturn(expectedTables);
        
        List<Table> result = tableService.getAllTables();
        
        assertEquals(expectedTables, result);
        verify(tableRepository).findAll();
    }
    
    @Test
    void testGetTableByIdSuccess() {
        when(tableRepository.findById(1L)).thenReturn(Optional.of(testTable));
        
        Optional<Table> result = tableService.getTableById(1L);
        
        assertTrue(result.isPresent());
        assertEquals(testTable, result.get());
        verify(tableRepository).findById(1L);
    }
    
    @Test
    void testGetTableByIdNotFound() {
        when(tableRepository.findById(999L)).thenReturn(Optional.empty());
        
        Optional<Table> result = tableService.getTableById(999L);
        
        assertFalse(result.isPresent());
        verify(tableRepository).findById(999L);
    }
    
    @Test
    void testGetTableByNumberSuccess() {
        when(tableRepository.findByNumber(1)).thenReturn(Optional.of(testTable));
        
        Optional<Table> result = tableService.getTableByNumber(1);
        
        assertTrue(result.isPresent());
        assertEquals(testTable, result.get());
        verify(tableRepository).findByNumber(1);
    }
    
    @Test
    void testGetTableByNumberNotFound() {
        when(tableRepository.findByNumber(999)).thenReturn(Optional.empty());
        
        Optional<Table> result = tableService.getTableByNumber(999);
        
        assertFalse(result.isPresent());
        verify(tableRepository).findByNumber(999);
    }
    
    @Test
    void testCreateTableSuccess() {
        when(tableRepository.save(any(Table.class))).thenReturn(testTable);
        
        Table result = tableService.createTable(testTable);
        
        assertEquals(testTable, result);
        verify(tableRepository).save(testTable);
    }
    
    @Test
    void testCreateTableInvalidNumber() {
        testTable.setNumber(0);
        
        assertThrows(IllegalArgumentException.class, () -> {
            tableService.createTable(testTable);
        });
        
        verify(tableRepository, never()).save(any(Table.class));
    }
    
    @Test
    void testCreateTableInvalidCapacity() {
        testTable.setCapacity(0);
        
        assertThrows(IllegalArgumentException.class, () -> {
            tableService.createTable(testTable);
        });
        
        verify(tableRepository, never()).save(any(Table.class));
    }
    
    @Test
    void testCreateTableCapacityTooHigh() {
        testTable.setCapacity(25);
        
        assertThrows(IllegalArgumentException.class, () -> {
            tableService.createTable(testTable);
        });
        
        verify(tableRepository, never()).save(any(Table.class));
    }
    
    @Test
    void testUpdateTableSuccess() {
        Table updateDetails = new Table();
        updateDetails.setNumber(2);
        updateDetails.setCapacity(6);
        
        when(tableRepository.findById(1L)).thenReturn(Optional.of(testTable));
        when(tableRepository.save(any(Table.class))).thenReturn(testTable);
        
        Table result = tableService.updateTable(1L, updateDetails);
        
        assertEquals(testTable, result);
        verify(tableRepository).findById(1L);
        verify(tableRepository).save(testTable);
    }
    
    @Test
    void testUpdateTableNotFound() {
        Table updateDetails = new Table();
        updateDetails.setNumber(2);
        updateDetails.setCapacity(6);
        
        when(tableRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () -> {
            tableService.updateTable(999L, updateDetails);
        });
        
        verify(tableRepository).findById(999L);
        verify(tableRepository, never()).save(any(Table.class));
    }
    
    @Test
    void testDeleteTableSuccess() {
        when(tableRepository.existsById(1L)).thenReturn(true);
        doNothing().when(tableRepository).deleteById(1L);
        
        tableService.deleteTable(1L);
        
        verify(tableRepository).existsById(1L);
        verify(tableRepository).deleteById(1L);
    }
    
    @Test
    void testDeleteTableNotFound() {
        when(tableRepository.existsById(999L)).thenReturn(false);
        
        assertThrows(IllegalArgumentException.class, () -> {
            tableService.deleteTable(999L);
        });
        
        verify(tableRepository).existsById(999L);
        verify(tableRepository, never()).deleteById(any(Long.class));
    }
    
    @Test
    void testTableExistsSuccess() {
        when(tableRepository.existsById(1L)).thenReturn(true);
        
        boolean result = tableService.tableExists(1L);
        
        assertTrue(result);
        verify(tableRepository).existsById(1L);
    }
    
    @Test
    void testTableExistsNotFound() {
        when(tableRepository.existsById(999L)).thenReturn(false);
        
        boolean result = tableService.tableExists(999L);
        
        assertFalse(result);
        verify(tableRepository).existsById(999L);
    }
    
    @Test
    void testTableExistsByNumberSuccess() {
        when(tableRepository.findByNumber(1)).thenReturn(Optional.of(testTable));
        
        boolean result = tableService.tableExistsByNumber(1);
        
        assertTrue(result);
        verify(tableRepository).findByNumber(1);
    }
    
    @Test
    void testTableExistsByNumberNotFound() {
        when(tableRepository.findByNumber(999)).thenReturn(Optional.empty());
        
        boolean result = tableService.tableExistsByNumber(999);
        
        assertFalse(result);
        verify(tableRepository).findByNumber(999);
    }
}
