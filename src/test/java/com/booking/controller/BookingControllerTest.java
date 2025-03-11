package com.booking.controller;

import com.booking.dataModel.dto.BookingDetails;
import com.booking.dataModel.dto.BookingDto;
import com.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void bookUnit_ShouldReturnBookingDetails() {
        BookingDto bookingDto = new BookingDto("user1", 1, Instant.now(), Instant.now());
        BookingDetails expectedDetails = new BookingDetails("user1", 1, Instant.now(), Instant.now(), null, null, "");
        when(bookingService.bookUnit(bookingDto)).thenReturn(expectedDetails);

        BookingDetails actualDetails = bookingController.bookUnit(bookingDto);

        assertNotNull(actualDetails);
        assertEquals(expectedDetails, actualDetails);
        verify(bookingService, times(1)).bookUnit(bookingDto);
    }

    @Test
    void cancelUnitBooking_ShouldReturnBookingDetails() {
        BookingDto bookingDto = new BookingDto("user1", 1, Instant.now(), Instant.now());
        BookingDetails expectedDetails = new BookingDetails("user1", 1, Instant.now(), Instant.now(), null, null, "");
        when(bookingService.cancelBooking(bookingDto)).thenReturn(expectedDetails);

        BookingDetails actualDetails = bookingController.cancelBooking(bookingDto);

        assertNotNull(actualDetails);
        assertEquals(expectedDetails, actualDetails);
        verify(bookingService, times(1)).cancelBooking(bookingDto);
    }
}

