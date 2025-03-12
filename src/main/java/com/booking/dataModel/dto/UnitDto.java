package com.booking.dataModel.dto;

import com.booking.dataModel.UnitType;

public record UnitDto(
        String username,
        int numRooms,
        UnitType type,
        int floor,
        double cost,
        String description
) {
}
