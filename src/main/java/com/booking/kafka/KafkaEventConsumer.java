package com.booking.kafka;

import com.booking.dataModel.dto.EventDto;
import com.booking.service.EventProcessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaEventConsumer {

    private final EventProcessService eventProcessService;
    private final ObjectMapper objectMapper;

    public KafkaEventConsumer(EventProcessService eventProcessService, ObjectMapper objectMapper) {
        this.eventProcessService = eventProcessService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "events-topic", groupId = "my-group")
    public void listen(ConsumerRecord<String, String> record) {
        System.out.println("Received message: " + record.value());
        try {
            var event = objectMapper.readValue(record.value(), EventDto.class);
            eventProcessService.processEvent(event);
        } catch (JsonProcessingException e) {
            log.error("Message: {} could not be decoded", record.value());
        }
    }
}