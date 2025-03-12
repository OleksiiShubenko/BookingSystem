package com.booking.service;

import com.booking.dataModel.*;
import com.booking.dataModel.dto.BookingDetails;
import com.booking.dataModel.dto.BookingDto;
import com.booking.dataModel.dto.EventDto;
import com.booking.dataModel.dto.PaymentDetails;
import com.booking.dataModel.exceptions.BookingException;
import com.booking.dataModel.exceptions.EntityNotFoundException;
import com.booking.kafka.KafkaEventProducer;
import com.booking.repository.BookingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UnitService unitService;
    private final UserService userService;
    private final UnitAvailabilityCacheService unitAvailabilityCacheService;
    private final KafkaEventProducer kafkaEventProducer;

    @Autowired
    public BookingService(
            BookingRepository bookingRepository,
            UnitService unitService,
            UserService userService,
            UnitAvailabilityCacheService unitAvailabilityCacheService,
            KafkaEventProducer kafkaEventProducer
    ) {
        this.bookingRepository = bookingRepository;
        this.unitService = unitService;
        this.userService = userService;
        this.unitAvailabilityCacheService = unitAvailabilityCacheService;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    public List<Booking> findAllOverlappedBookings(List<Integer> unitIds, Instant fromTime, Instant toTime) {
        return bookingRepository.findAllOverlappedBookings(unitIds, fromTime, toTime, BookingStatus.BOOKED);
    }

    /**
     * Book unit providing booking parameters
     */
    @Transactional
    public BookingDetails bookUnit(BookingDto bookingDto) {
        if (unitAvailabilityCacheService.getAvailableUnits() < 1) {
            throw new BookingException("Unit " + bookingDto.unitId() + " could not be booked. All units are booked!");
        }

        //check availability by a range of dates
        List<Booking> overlappedBookings = bookingRepository.findAllOverlappedBookings(bookingDto.unitId(), bookingDto.fromTime(), bookingDto.toTime(), BookingStatus.BOOKED);

        if (!overlappedBookings.isEmpty()) {
            String overlappedMessage = overlappedBookings.stream()
                    .map(it -> "\n" + it.getFromTime() + ":" + it.getToTime() + "\n")
                    .collect(Collectors.joining(","));
            throw new BookingException("Provided booking time - " + bookingDto.fromTime() + ":" + bookingDto.toTime() + " is overlapped with time ranges: " + overlappedMessage);
        } else {
            var unit = unitService.getUnit(bookingDto.unitId());
            var user = userService.getUser(bookingDto.username());

            var payment = new Payment();
            payment.setTransactionId(generateTransactionId());
            payment.setCost(unit.getIncreasedCost());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setCreatedAt(Instant.now());

            Booking booking = Booking.builder()
                    .unit(unit)
                    .user(user)
                    .fromTime(bookingDto.fromTime())
                    .toTime(bookingDto.toTime())
                    .status(BookingStatus.BOOKED)
                    .payment(payment)
                    .bookingTime(Instant.now())
                    .build();

            payment.setBooking(booking);

            bookingRepository.save(booking);

            kafkaEventProducer.generateEvent(new EventDto(payment.getTransactionId(), EventType.BOOKING_CREATED));
            return bookingDetails(booking, "Booking is created", "Payment with transactionId: " + booking.getPayment().getTransactionId() + " is created.");
        }
    }

    /**
     * Cancel booking providing existence booking parameter
     */
    public BookingDetails cancelBooking(BookingDto bookingDto) {
        //need to fetch only booked
        Booking currentBooking = bookingRepository.findByUserNameAndUnitIdAndFromTimeAndToTimeAndStatus(
                bookingDto.username(),
                bookingDto.unitId(),
                bookingDto.fromTime(),
                bookingDto.toTime()
        );
        if (currentBooking == null) {
            throw new EntityNotFoundException("Booking with username: " + bookingDto.username() +
                    ", unitId: " + bookingDto.unitId() +
                    ", fromTime: " + bookingDto.fromTime() +
                    "and toTime: " + bookingDto.toTime() + " does nit exist");
        } else if (currentBooking.getStatus().equals(BookingStatus.BOOKED)) { //&& currentBooking.getPayment().getStatus().equals(PaymentStatus.PENDING)// don't understand exactly when booking can be cancelled, always, or during payment is in PENDING
            //generate event to cancel booking and payment
            currentBooking.setStatus(BookingStatus.CANCELLING_IN_PROGRESS);
            bookingRepository.save(currentBooking);

            kafkaEventProducer.generateEvent(new EventDto(currentBooking.getPayment().getTransactionId(), EventType.BOOKING_CANCELLED));

            return bookingDetails(currentBooking, "Booking canceling in progress", "Payment cancelling in progress");
        } else {
            return bookingDetails(currentBooking, "Booking could not be cancelled due to incorrect status", "Payment could not be cancelled due to incorrect status");
        }
    }

    private BookingDetails bookingDetails(Booking booking, String bookingDescription, String paymentDescription) {
        var paymentDetails = new PaymentDetails(
                booking.getPayment().getTransactionId(),
                booking.getPayment().getCost(),
                booking.getPayment().getStatus(),
                booking.getPayment().getCreatedAt(),
                paymentDescription
        );

        return new BookingDetails(
                booking.getUser().getUsername(),
                booking.getUnit().getId(),
                booking.getFromTime(),
                booking.getToTime(),
                booking.getStatus(),
                paymentDetails,
                bookingDescription
        );
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}
