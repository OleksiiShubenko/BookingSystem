package com.booking.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class UnitAvailabilityCacheService {

    private static final String AVAILABLE_UNITS_KEY = "available_units";

    private final UnitService unitService;
    private final ValueOperations<String, Long> valueOperations;

    @Autowired
    public UnitAvailabilityCacheService(UnitService unitService, RedisTemplate<String, Long> redisTemplate) {
        this.unitService = unitService;
        valueOperations = redisTemplate.opsForValue();
    }

    @PostConstruct
    public void fetchUnitsAmount() {
        var unitAmount = unitService.countAll();
        unitAmount = unitAmount == 0 ? 100 : unitAmount;
        valueOperations.set(AVAILABLE_UNITS_KEY, unitAmount);
    }

    public void increaseAvailableUnits() {
        valueOperations.set(AVAILABLE_UNITS_KEY, getAvailableUnits() + 1);
    }

    public void decreaseAvailableUnits() {
        valueOperations.set(AVAILABLE_UNITS_KEY, getAvailableUnits() - 1);
    }

    public Long getAvailableUnits() {
        return valueOperations.get(AVAILABLE_UNITS_KEY);
    }
}
