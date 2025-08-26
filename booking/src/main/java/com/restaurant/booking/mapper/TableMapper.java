package com.restaurant.booking.mapper;

import org.springframework.stereotype.Component;

import com.restaurant.booking.dto.TableDTO;
import com.restaurant.booking.model.Table;

@Component
public class TableMapper {
    
    public TableDTO toDto(Table table) {
        if (table == null) {
            return null;
        }
        
        return new TableDTO(
            table.getId(),
            table.getNumber(),
            table.getCapacity()
        );
    }
    
    public Table toEntity(TableDTO tableDto) {
        if (tableDto == null) {
            return null;
        }
        
        Table table = new Table();
        table.setId(tableDto.getId());
        table.setNumber(tableDto.getNumber());
        table.setCapacity(tableDto.getCapacity());
        
        return table;
    }
}
