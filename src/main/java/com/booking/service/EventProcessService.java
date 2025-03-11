package com.booking.service;

import com.booking.dataModel.Event;
import com.booking.dataModel.EventType;
import com.booking.dataModel.dto.EventDto;
import com.booking.kafka.KafkaEventProducer;
import com.booking.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Event processor service, which listen kafka topic
 */
@Service
public class EventProcessService {

    private final PaymentService paymentService;
    private final EventRepository eventRepository;
    private final KafkaEventProducer kafkaEventProducer;
    private final UnitAvailabilityCacheService unitAvailabilityCacheService;

    private final ConcurrentHashMap<String, Timer> timerHolder = new ConcurrentHashMap<>();

    public EventProcessService(
            PaymentService paymentService,
            EventRepository eventRepository,
            KafkaEventProducer kafkaEventProducer,
            UnitAvailabilityCacheService unitAvailabilityCacheService
    ) {
        this.paymentService = paymentService;
        this.eventRepository = eventRepository;
        this.kafkaEventProducer = kafkaEventProducer;
        this.unitAvailabilityCacheService = unitAvailabilityCacheService;
    }

    /**
     * Process incoming events depending on event type
     */
    public void processEvent(EventDto eventDto) {
        if (EventType.BOOKING_CREATED.equals(eventDto.getEventType())) {
            setPaymentTimer(eventDto.getTransactionId());
        } else if (EventType.PAYMENT_SUCCESS.equals(eventDto.getEventType())) {
            setSuccessfulPayment(eventDto.getTransactionId());
        } else if (EventType.BOOKING_CANCELLED.equals(eventDto.getEventType())) {
            cancelPayment(eventDto.getTransactionId());
        }

        var event = new Event(
                eventDto.getEventType(),
                Instant.now(),
                eventDto.getTransactionId()
        );
        eventRepository.save(event);
    }

    private void cancelPayment(String transactionId) {
        paymentService.cancelPayment(transactionId);
        Optional.ofNullable(timerHolder.get(transactionId)).ifPresent(Timer::cancel);
        unitAvailabilityCacheService.increaseAvailableUnits();
    }

    private void setSuccessfulPayment(String transactionId) {
        paymentService.setSuccessfulPayment(transactionId);
        Optional.ofNullable(timerHolder.get(transactionId)).ifPresent(Timer::cancel);
        unitAvailabilityCacheService.decreaseAvailableUnits();
    }


    private void setPaymentTimer(String transactionId) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                kafkaEventProducer.generateEvent(new EventDto(transactionId, EventType.BOOKING_CANCELLED));
            }
        };
        // uncomment line with 1-minute timer to test is faster
        timer.schedule(task, 15 * 60 * 1000);
//        timer.schedule(task, 1 * 60 * 1000);
        timerHolder.put(transactionId, timer);
    }

    public ConcurrentHashMap<String, Timer> getTimerHolder() {
        return timerHolder;
    }
}
