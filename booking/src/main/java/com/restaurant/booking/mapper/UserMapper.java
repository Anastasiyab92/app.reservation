package com.restaurant.booking.mapper;

import org.springframework.stereotype.Component;

import com.restaurant.booking.dto.UserDTO;
import com.restaurant.booking.model.User;

@Component
public class UserMapper {
    
    public UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }
        
        return new UserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhoneNumber()
        );
    }
    
    public User toEntity(UserDTO userDto) {
        if (userDto == null) {
            return null;
        }
        
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        
        return user;
    }
}
