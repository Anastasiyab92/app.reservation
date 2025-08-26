package com.restaurant.booking.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import com.restaurant.booking.dto.TableDTO;
import com.restaurant.booking.model.Table;
import com.restaurant.booking.repository.TableRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=false",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TableControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TableRepository tableRepository;

    @BeforeEach
    void setUp() {
        tableRepository.deleteAll();
    }

    @Test
    void testGetAllTables() {
        // Create test table
        Table table = new Table();
        table.setNumber(1);
        table.setCapacity(4);
        tableRepository.save(table);

        ResponseEntity<TableDTO[]> response = restTemplate.getForEntity("/api/tables", TableDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TableDTO[] tables = response.getBody();
        assertNotNull(tables);
        assertEquals(1, tables.length);
        assertEquals(1, tables[0].getNumber());
        assertEquals(4, tables[0].getCapacity());
    }

    @Test
    void testGetAllTablesEmpty() {
        ResponseEntity<TableDTO[]> response = restTemplate.getForEntity("/api/tables", TableDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TableDTO[] tables = response.getBody();
        assertNotNull(tables);
        assertEquals(0, tables.length);
    }

    @Test
    void testGetTableById() {
        // Create test table
        Table table = new Table();
        table.setNumber(1);
        table.setCapacity(4);
        Table savedTable = tableRepository.save(table);

        ResponseEntity<TableDTO> response = restTemplate.getForEntity("/api/tables/" + savedTable.getId(), TableDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TableDTO tableDto = response.getBody();
        assertNotNull(tableDto);
        assertEquals(savedTable.getId(), tableDto.getId());
        assertEquals(1, tableDto.getNumber());
        assertEquals(4, tableDto.getCapacity());
    }

    @Test
    void testGetTableByIdNotFound() {
        ResponseEntity<TableDTO> response = restTemplate.getForEntity("/api/tables/999", TableDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetTableByNumber() {
        // Create test table
        Table table = new Table();
        table.setNumber(5);
        table.setCapacity(6);
        tableRepository.save(table);

        ResponseEntity<TableDTO> response = restTemplate.getForEntity("/api/tables/number/5", TableDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TableDTO tableDto = response.getBody();
        assertNotNull(tableDto);
        assertEquals(5, tableDto.getNumber());
        assertEquals(6, tableDto.getCapacity());
    }

    @Test
    void testGetTableByNumberNotFound() {
        ResponseEntity<TableDTO> response = restTemplate.getForEntity("/api/tables/number/999", TableDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateTable() {
        Table table = new Table();
        table.setNumber(3);
        table.setCapacity(8);

        ResponseEntity<TableDTO> response = restTemplate.postForEntity("/api/tables", table, TableDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        TableDTO tableDto = response.getBody();
        assertNotNull(tableDto);
        assertNotNull(tableDto.getId());
        assertEquals(3, tableDto.getNumber());
        assertEquals(8, tableDto.getCapacity());
    }

    @Test
    void testCreateTableInvalidData() {
        Table table = new Table();
        table.setNumber(0); // Invalid number
        table.setCapacity(4);

        ResponseEntity<TableDTO> response = restTemplate.postForEntity("/api/tables", table, TableDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateTable() {
        // Create test table
        Table table = new Table();
        table.setNumber(1);
        table.setCapacity(4);
        Table savedTable = tableRepository.save(table);

        // Update data
        Table updateData = new Table();
        updateData.setNumber(2);
        updateData.setCapacity(6);

        ResponseEntity<TableDTO> response = restTemplate.exchange(
            "/api/tables/" + savedTable.getId(),
            org.springframework.http.HttpMethod.PUT,
            new org.springframework.http.HttpEntity<>(updateData),
            TableDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TableDTO tableDto = response.getBody();
        assertNotNull(tableDto);
        assertEquals(savedTable.getId(), tableDto.getId());
        assertEquals(2, tableDto.getNumber());
        assertEquals(6, tableDto.getCapacity());
    }

    @Test
    void testUpdateTableNotFound() {
        Table updateData = new Table();
        updateData.setNumber(2);
        updateData.setCapacity(6);

        ResponseEntity<TableDTO> response = restTemplate.exchange(
            "/api/tables/999",
            org.springframework.http.HttpMethod.PUT,
            new org.springframework.http.HttpEntity<>(updateData),
            TableDTO.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteTable() {
        // Create test table
        Table table = new Table();
        table.setNumber(1);
        table.setCapacity(4);
        Table savedTable = tableRepository.save(table);

        ResponseEntity<Void> response = restTemplate.exchange(
            "/api/tables/" + savedTable.getId(),
            org.springframework.http.HttpMethod.DELETE,
            null,
            Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        
        // Verify table is deleted
        assertFalse(tableRepository.existsById(savedTable.getId()));
    }

    @Test
    void testDeleteTableNotFound() {
        ResponseEntity<Void> response = restTemplate.exchange(
            "/api/tables/999",
            org.springframework.http.HttpMethod.DELETE,
            null,
            Void.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCheckTableExists() {
        // Create test table
        Table table = new Table();
        table.setNumber(1);
        table.setCapacity(4);
        Table savedTable = tableRepository.save(table);

        ResponseEntity<Boolean> response = restTemplate.getForEntity("/api/tables/exists/" + savedTable.getId(), Boolean.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Boolean exists = response.getBody();
        assertNotNull(exists);
        assertTrue(exists);
    }

    @Test
    void testCheckTableExistsNotFound() {
        ResponseEntity<Boolean> response = restTemplate.getForEntity("/api/tables/exists/999", Boolean.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Boolean exists = response.getBody();
        assertNotNull(exists);
        assertFalse(exists);
    }

    @Test
    void testCheckTableExistsByNumber() {
        // Create test table
        Table table = new Table();
        table.setNumber(7);
        table.setCapacity(4);
        tableRepository.save(table);

        ResponseEntity<Boolean> response = restTemplate.getForEntity("/api/tables/exists/number/7", Boolean.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Boolean exists = response.getBody();
        assertNotNull(exists);
        assertTrue(exists);
    }

    @Test
    void testCheckTableExistsByNumberNotFound() {
        ResponseEntity<Boolean> response = restTemplate.getForEntity("/api/tables/exists/number/999", Boolean.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Boolean exists = response.getBody();
        assertNotNull(exists);
        assertFalse(exists);
    }
}
