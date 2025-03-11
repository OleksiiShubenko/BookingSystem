package com.booking.service;

import com.booking.dataModel.Event;
import com.booking.dataModel.EventType;
import com.booking.dataModel.dto.EventDto;
import com.booking.kafka.KafkaEventProducer;
import com.booking.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Timer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventProcessServiceTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private KafkaEventProducer kafkaEventProducer;

    @Mock
    private UnitAvailabilityCacheService unitAvailabilityCacheService;

    @InjectMocks
    private EventProcessService eventProcessService;

    @Test
    void shouldProcessBookingCreatedEventAndSetPaymentTimer_whenBookingCreatedEventIsSent() {
        var eventDto = new EventDto("12345", EventType.BOOKING_CREATED);

        eventProcessService.processEvent(eventDto);

        assertEquals(1, eventProcessService.getTimerHolder().size());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void shouldProcessPaymentSuccessEventAndSetPaymentTimer_whenPaymentSuccessEventIsSent() {
        var eventDto = new EventDto("12345", EventType.PAYMENT_SUCCESS);
        eventProcessService.getTimerHolder().put("12345", new Timer());

        eventProcessService.processEvent(eventDto);

        verify(paymentService).setSuccessfulPayment("12345");
        verify(unitAvailabilityCacheService).decreaseAvailableUnits();
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void shouldProcessBookingCanceledEventAndSetPaymentTimer_whenBookingCanceledEventIsSent() {
        var eventDto = new EventDto("12345", EventType.BOOKING_CANCELLED);
        eventProcessService.getTimerHolder().put("12345", new Timer());

        eventProcessService.processEvent(eventDto);

        verify(paymentService).cancelPayment("12345");
        verify(unitAvailabilityCacheService).increaseAvailableUnits();
        verify(eventRepository).save(any(Event.class));
    }
}