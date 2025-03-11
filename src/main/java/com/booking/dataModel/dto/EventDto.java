package com.booking.dataModel.dto;

import com.booking.dataModel.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private String transactionId;
    private EventType eventType;
//
//    public EventDto() {
//    }
//
//    public EventDto(String transactionId, EventType eventType) {
//        this.transactionId = transactionId;
//        this.eventType = eventType;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (o == null || getClass() != o.getClass()) return false;
//        EventDto eventDto = (EventDto) o;
//        return Objects.equals(transactionId, eventDto.transactionId) && eventType == eventDto.eventType;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(transactionId, eventType);
//    }
//
//    public String getTransactionId() {
//        return transactionId;
//    }
//
//    public void setTransactionId(String transactionId) {
//        this.transactionId = transactionId;
//    }
//
//    public EventType getEventType() {
//        return eventType;
//    }
//
//    public void setEventType(EventType eventType) {
//        this.eventType = eventType;
//    }
//
//    @Override
//    public String toString() {
//        return "EventDto{" +
//                "transactionId='" + transactionId + '\'' +
//                ", eventType=" + eventType +
//                '}';
//    }
}
