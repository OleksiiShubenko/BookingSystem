package com.booking.dataModel.dto;

import com.booking.dataModel.UnitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitSearchParams {

    private Double minCost;
    private Double maxCost;
    private Integer numRooms;
    private Integer floor;
    private Instant fromTime;
    private Instant toTime;
    private UnitType type;
    private UnitSort sortBy;
    private Sort.Direction sortOrder;

}
