package com.booking.kafka;

import com.booking.dataModel.EventType;
import com.booking.dataModel.dto.EventDto;
import com.booking.service.EventProcessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaEventConsumerTest {

    @Mock
    private EventProcessService eventProcessService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaEventConsumer kafkaEventConsumer;

    @Test
    void listen_ShouldProcessValidEvent() throws JsonProcessingException {
        String jsonMessage = "{\"event\":\"TestEvent\"}";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("events-topic", 0, 0, "key", jsonMessage);
        EventDto expectedEvent = new EventDto("xxxx-xxxx-xxxx-0001", EventType.BOOKING_CREATED);
        when(objectMapper.readValue(jsonMessage, EventDto.class)).thenReturn(expectedEvent);

        kafkaEventConsumer.listen(record);

        verify(eventProcessService, times(1)).processEvent(expectedEvent);
    }

    @Test
    void listen_ShouldHandleJsonProcessingException() throws JsonProcessingException {
        String invalidJsonMessage = "invalid json";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("events-topic", 0, 0, "key", invalidJsonMessage);
        when(objectMapper.readValue(invalidJsonMessage, EventDto.class)).thenThrow(new JsonProcessingException("Error") {
        });

        assertDoesNotThrow(() -> kafkaEventConsumer.listen(record));
        verify(eventProcessService, never()).processEvent(any());
    }
}

