package com.booking.kafka;

import com.booking.dataModel.EventType;
import com.booking.dataModel.dto.EventDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaEventProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaEventProducer kafkaEventProducer;

    @Test
    void generateEvent_ShouldSendMessageToKafka() throws JsonProcessingException {
        EventDto eventDto = new EventDto("xxxx-xxxx-xxxx-0001", EventType.BOOKING_CREATED);
        String eventJson = "{\"event\":\"TestEvent\"}";
        when(objectMapper.writeValueAsString(eventDto)).thenReturn(eventJson);

        kafkaEventProducer.generateEvent(eventDto);

        verify(kafkaTemplate, times(1)).send("events-topic", eventJson);
    }

    @Test
    void generateEvent_ShouldHandleJsonProcessingException() throws JsonProcessingException {
        EventDto eventDto = new EventDto("xxxx-xxxx-xxxx-0001", EventType.PAYMENT_SUCCESS);
        when(objectMapper.writeValueAsString(eventDto)).thenThrow(new JsonProcessingException("Error") {
        });

        assertDoesNotThrow(() -> kafkaEventProducer.generateEvent(eventDto));
    }
}
