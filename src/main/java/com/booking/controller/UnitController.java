package com.booking.controller;

import com.booking.dataModel.Unit;
import com.booking.dataModel.dto.UnitAvailabilityInfo;
import com.booking.dataModel.dto.UnitDto;
import com.booking.dataModel.dto.UnitSearchParams;
import com.booking.dataModel.exceptions.ErrorResponse;
import com.booking.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/unit")
@Tag(name = "Unit API", description = "Unit operations")
public class UnitController {

    private final UnitService unitService;

    @Autowired
    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @Operation(
            summary = "Create a new unit",
            description = "Creates a new unit based on the provided unit details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Unit created successfully",
                            content = @Content(schema = @Schema(implementation = Unit.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping
    public UnitDto createUnit(@RequestBody UnitDto unitDto) {
        return unitService.createUnit(unitDto);
    }

    @Operation(
            summary = "Return units by search options",
            description = "Return units by provided search options",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Units are fetched successfully",
                            content = @Content(schema = @Schema(implementation = Unit.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/search")
    public List<UnitDto> searchUnits(
            UnitSearchParams params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return unitService.getUnits(params, page, size);
    }

    @Operation(
            summary = "Return units availability info",
            description = "Return units availability info",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Units are fetched successfully",
                            content = @Content(schema = @Schema(implementation = Unit.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/availability")
    public UnitAvailabilityInfo getUnitAvailabilityInfo() {
        return unitService.getUnitAvailabilityInfo();
    }
}
