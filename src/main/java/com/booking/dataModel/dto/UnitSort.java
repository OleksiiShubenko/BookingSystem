package com.booking.dataModel.dto;

public enum UnitSort {
    NUM_ROOMS("numRooms"),
    TYPE("type"),
    FLOOR("floor"),
    COST("cost");

    UnitSort(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
