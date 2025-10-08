package com.restaurant.booking.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.restaurant.booking.dto.TableDTO;
import com.restaurant.booking.model.Table;

class TableMapperTest {

    private TableMapper tableMapper;

    @BeforeEach
    void setUp() {
        tableMapper = new TableMapper();
    }

    @Test
    void toDto_ValidTable_ReturnsCorrectDTO() {
        // Given
        Table table = new Table();
        table.setId(1L);
        table.setNumber(5);
        table.setCapacity(4);

        // When
        TableDTO result = tableMapper.toDto(table);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(5, result.getNumber());
        assertEquals(4, result.getCapacity());
    }

    @Test
    void toDto_NullTable_ReturnsNull() {
        // When
        TableDTO result = tableMapper.toDto(null);

        // Then
        assertNull(result);
    }

    @Test
    void toDto_TableWithZeroValues_HandlesCorrectly() {
        // Given
        Table table = new Table();
        table.setId(0L);
        table.setNumber(0);
        table.setCapacity(0);

        // When
        TableDTO result = tableMapper.toDto(table);

        // Then
        assertNotNull(result);
        assertEquals(0L, result.getId());
        assertEquals(0, result.getNumber());
        assertEquals(0, result.getCapacity());
    }

    @Test
    void toDto_TableWithMaxValues_HandlesCorrectly() {
        // Given
        Table table = new Table();
        table.setId(Long.MAX_VALUE);
        table.setNumber(Integer.MAX_VALUE);
        table.setCapacity(Integer.MAX_VALUE);

        // When
        TableDTO result = tableMapper.toDto(table);

        // Then
        assertNotNull(result);
        assertEquals(Long.MAX_VALUE, result.getId());
        assertEquals(Integer.MAX_VALUE, result.getNumber());
        assertEquals(Integer.MAX_VALUE, result.getCapacity());
    }

    @Test
    void toEntity_ValidDTO_ReturnsCorrectEntity() {
        // Given
        TableDTO tableDTO = new TableDTO(2L, 8, 6);

        // When
        Table result = tableMapper.toEntity(tableDTO);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(8, result.getNumber());
        assertEquals(6, result.getCapacity());
    }

    @Test
    void toEntity_NullDTO_ReturnsNull() {
        // When
        Table result = tableMapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    void toEntity_DTOWithZeroValues_HandlesCorrectly() {
        // Given
        TableDTO tableDTO = new TableDTO(0L, 0, 0);

        // When
        Table result = tableMapper.toEntity(tableDTO);

        // Then
        assertNotNull(result);
        assertEquals(0L, result.getId());
        assertEquals(0, result.getNumber());
        assertEquals(0, result.getCapacity());
    }

    @Test
    void toEntity_DTOWithMaxValues_HandlesCorrectly() {
        // Given
        TableDTO tableDTO = new TableDTO(Long.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

        // When
        Table result = tableMapper.toEntity(tableDTO);

        // Then
        assertNotNull(result);
        assertEquals(Long.MAX_VALUE, result.getId());
        assertEquals(Integer.MAX_VALUE, result.getNumber());
        assertEquals(Integer.MAX_VALUE, result.getCapacity());
    }

    @Test
    void toDto_AllFields_AllFieldsMappedCorrectly() {
        // Given
        Table table = new Table();
        table.setId(999L);
        table.setNumber(15);
        table.setCapacity(10);

        // When
        TableDTO result = tableMapper.toDto(table);

        // Then
        assertNotNull(result);
        assertEquals(table.getId(), result.getId());
        assertEquals(table.getNumber(), result.getNumber());
        assertEquals(table.getCapacity(), result.getCapacity());
    }

    @Test
    void toEntity_AllFields_AllFieldsMappedCorrectly() {
        // Given
        TableDTO tableDTO = new TableDTO(888L, 20, 12);

        // When
        Table result = tableMapper.toEntity(tableDTO);

        // Then
        assertNotNull(result);
        assertEquals(tableDTO.getId(), result.getId());
        assertEquals(tableDTO.getNumber(), result.getNumber());
        assertEquals(tableDTO.getCapacity(), result.getCapacity());
    }

    @Test
    void toDto_TableWithNegativeValues_HandlesCorrectly() {
        // Given
        Table table = new Table();
        table.setId(-1L);
        table.setNumber(-5);
        table.setCapacity(-2);

        // When
        TableDTO result = tableMapper.toDto(table);

        // Then
        assertNotNull(result);
        assertEquals(-1L, result.getId());
        assertEquals(-5, result.getNumber());
        assertEquals(-2, result.getCapacity());
    }

    @Test
    void toEntity_DTOWithNegativeValues_HandlesCorrectly() {
        // Given
        TableDTO tableDTO = new TableDTO(-2L, -10, -8);

        // When
        Table result = tableMapper.toEntity(tableDTO);

        // Then
        assertNotNull(result);
        assertEquals(-2L, result.getId());
        assertEquals(-10, result.getNumber());
        assertEquals(-8, result.getCapacity());
    }
}


