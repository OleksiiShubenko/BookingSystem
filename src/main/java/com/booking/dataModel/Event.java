package com.booking.dataModel;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private Instant timestamp;

    private String transactionId;

    public Event(EventType eventType, Instant timestamp, String transactionId) {
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.transactionId = transactionId;
    }
}
