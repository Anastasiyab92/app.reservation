package com.restaurant.booking.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.booking.dto.TableDTO;
import com.restaurant.booking.mapper.TableMapper;
import com.restaurant.booking.model.Table;
import com.restaurant.booking.service.TableService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
@Tag(name = "Table Management", description = "APIs for managing restaurant tables")
public class TableController {
    
    private final TableService tableService;
    private final TableMapper tableMapper;
    
    @GetMapping
    @Operation(summary = "Get all tables", description = "Retrieves a list of all available tables")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of tables retrieved successfully",
            content = @Content(schema = @Schema(implementation = TableDTO.class)))
    })
    public ResponseEntity<List<TableDTO>> getAllTables() {
        List<TableDTO> tables = tableService.getAllTables().stream()
            .map(tableMapper::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(tables);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TableDTO> getTableById(@PathVariable Long id) {
        return tableService.getTableById(id)
            .map(table -> ResponseEntity.ok(tableMapper.toDto(table)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/number/{number}")
    public ResponseEntity<TableDTO> getTableByNumber(@PathVariable int number) {
        return tableService.getTableByNumber(number)
            .map(table -> ResponseEntity.ok(tableMapper.toDto(table)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Create a new table", description = "Creates a new table with the specified number and capacity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Table created successfully",
            content = @Content(schema = @Schema(implementation = TableDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<TableDTO> createTable(@Valid @RequestBody Table table) {
        Table savedTable = tableService.createTable(table);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(tableMapper.toDto(savedTable));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TableDTO> updateTable(@PathVariable Long id, @Valid @RequestBody Table table) {
        try {
            Table updatedTable = tableService.updateTable(id, table);
            return ResponseEntity.ok(tableMapper.toDto(updatedTable));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        try {
            tableService.deleteTable(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> checkTableExists(@PathVariable Long id) {
        boolean exists = tableService.tableExists(id);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/exists/number/{number}")
    public ResponseEntity<Boolean> checkTableExistsByNumber(@PathVariable int number) {
        boolean exists = tableService.tableExistsByNumber(number);
        return ResponseEntity.ok(exists);
    }
}
