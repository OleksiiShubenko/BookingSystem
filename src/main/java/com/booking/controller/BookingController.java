package com.booking.controller;

import com.booking.dataModel.dto.BookingDetails;
import com.booking.dataModel.dto.BookingDto;
import com.booking.dataModel.exceptions.ErrorResponse;
import com.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/booking")
@Tag(name = "Booking API", description = "Booking operations")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(
            summary = "Book unit",
            description = "Create booking based on provided attributes",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Booking is successful",
                            content = @Content(schema = @Schema(implementation = BookingDetails.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping
    public BookingDetails bookUnit(@RequestBody BookingDto bookingDto) {
        return bookingService.bookUnit(bookingDto);
    }

    @Operation(
            summary = "Cancel unit booking",
            description = "Cancel unit booking based on provided attributes",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Booking cancelling is successful",
                            content = @Content(schema = @Schema(implementation = BookingDetails.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PutMapping(value = "/cancel")
    public BookingDetails cancelBooking(@RequestBody BookingDto bookingDto) {
        return bookingService.cancelBooking(bookingDto);
    }

}
