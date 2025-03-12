package com.booking.service;

import com.booking.dataModel.*;
import com.booking.dataModel.dto.BookingDetails;
import com.booking.dataModel.dto.BookingDto;
import com.booking.dataModel.dto.EventDto;
import com.booking.dataModel.exceptions.BookingException;
import com.booking.dataModel.exceptions.EntityNotFoundException;
import com.booking.kafka.KafkaEventProducer;
import com.booking.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UnitService unitService;

    @Mock
    private UserService userService;

    @Mock
    private UnitAvailabilityCacheService unitAvailabilityCacheService;

    @Mock
    private KafkaEventProducer kafkaEventProducer;

    @InjectMocks
    private BookingService bookingService;

    @Test
    public void shouldBookUnit_Success() {
        BookingDto bookingDto = new BookingDto("user1", 1, Instant.now(), Instant.now().plusSeconds(3600));
        Unit unit = new Unit(1, 2, UnitType.APARTMENTS, 1, 100.0, "Description", null, null);
        User user = new User("user1", "password", null, null);
        Payment payment = new Payment();
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setCost(unit.getIncreasedCost());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(Instant.now());

        when(unitAvailabilityCacheService.getAvailableUnits()).thenReturn(10L);
        when(bookingRepository.findAllOverlappedBookings(anyInt(), any(), any(), eq(BookingStatus.BOOKED))).thenReturn(Collections.emptyList());
        when(unitService.getUnit(bookingDto.unitId())).thenReturn(unit);
        when(userService.getUser(bookingDto.username())).thenReturn(user);
        when(bookingRepository.save(any(Booking.class))).thenReturn(new Booking());

        BookingDetails bookingDetails = bookingService.bookUnit(bookingDto);

        assertNotNull(bookingDetails);
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(kafkaEventProducer, times(1)).generateEvent(any(EventDto.class));
    }

    @Test
    public void shouldBookUnit_whenUnitNotAvailable() {
        BookingDto bookingDto = new BookingDto("user1", 1, Instant.now(), Instant.now().plusSeconds(3600));

        when(unitAvailabilityCacheService.getAvailableUnits()).thenReturn(0L);

        BookingException exception = assertThrows(BookingException.class, () -> bookingService.bookUnit(bookingDto));
        assertEquals("Unit 1 could not be booked. All units are booked!", exception.getMessage());
    }

    @Test
    public void shouldBookUnit_whenOverlapWithExistingBooking() {
        BookingDto bookingDto = new BookingDto("user1", 1, Instant.now(), Instant.now().plusSeconds(3600));
        Booking existingBooking = new Booking();
        existingBooking.setFromTime(Instant.now());
        existingBooking.setToTime(Instant.now().plusSeconds(3600));
        existingBooking.setStatus(BookingStatus.BOOKED);

        when(unitAvailabilityCacheService.getAvailableUnits()).thenReturn(10L);
        when(bookingRepository.findAllOverlappedBookings(anyInt(), any(), any(), eq(BookingStatus.BOOKED)))
                .thenReturn(Collections.singletonList(existingBooking));

        BookingException exception = assertThrows(BookingException.class, () -> bookingService.bookUnit(bookingDto));
        assertTrue(exception.getMessage().contains("Provided booking time"));
    }

    @Test
    public void shouldCancelBooking_whenAllIsSuccess() {
        BookingDto bookingDto = new BookingDto("user1", 1, Instant.now(), Instant.now().plusSeconds(3600));
        Booking existingBooking = Booking.builder()
                .status(BookingStatus.BOOKED)
                .payment(new Payment())
                .user(User.builder().username("user1").build())
                .unit(Unit.builder().id(1).build())
                .build();
        when(bookingRepository.findByUserNameAndUnitIdAndFromTimeAndToTimeAndStatus(
                bookingDto.username(), bookingDto.unitId(), bookingDto.fromTime(), bookingDto.toTime(), BookingStatus.BOOKED
        )).thenReturn(existingBooking);

        BookingDetails bookingDetails = bookingService.cancelBooking(bookingDto);

        assertNotNull(bookingDetails);
        assertEquals("Booking canceling in progress", bookingDetails.description());
        verify(bookingRepository, times(1)).save(existingBooking);
        verify(kafkaEventProducer, times(1)).generateEvent(any(EventDto.class));
    }

    @Test
    public void shouldCancelBooking_whenBookingNotFound() {
        BookingDto bookingDto = new BookingDto("user1", 1, Instant.now(), Instant.now().plusSeconds(3600));

        when(bookingRepository.findByUserNameAndUnitIdAndFromTimeAndToTimeAndStatus(
                bookingDto.username(), bookingDto.unitId(), bookingDto.fromTime(), bookingDto.toTime(), BookingStatus.BOOKED
        )).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> bookingService.cancelBooking(bookingDto));
        assertEquals("Booking with username: user1, unitId: 1, fromTime: " + bookingDto.fromTime() +
                "and toTime: " + bookingDto.toTime() + " does nit exist", exception.getMessage());
    }
}
