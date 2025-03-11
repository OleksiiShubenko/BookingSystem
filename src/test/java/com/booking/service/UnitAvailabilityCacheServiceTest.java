package com.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UnitAvailabilityCacheServiceTest {

    @Mock
    private UnitService unitService;

    @Mock
    private RedisTemplate<String, Long> redisTemplate;

    @Mock
    private ValueOperations<String, Long> valueOperations;

    private UnitAvailabilityCacheService unitAvailabilityCacheService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        unitAvailabilityCacheService = new UnitAvailabilityCacheService(unitService, redisTemplate);
    }

    @Test
    void shouldFetchUnitsAmountAndSetItToCache() {
        when(unitService.countAll()).thenReturn(50L);

        unitAvailabilityCacheService.fetchUnitsAmount();

        verify(valueOperations).set("available_units", 50L);
    }

    @Test
    void shouldSetDefaultUnitsAmountIfUnitCountIsZero() {
        when(unitService.countAll()).thenReturn(0L);

        unitAvailabilityCacheService.fetchUnitsAmount();

        verify(valueOperations).set("available_units", 100L);
    }

    @Test
    void shouldIncreaseAvailableUnits() {
        when(valueOperations.get("available_units")).thenReturn(10L);

        unitAvailabilityCacheService.increaseAvailableUnits();

        verify(valueOperations).set("available_units", 11L);
    }

    @Test
    void shouldDecreaseAvailableUnits() {
        when(valueOperations.get("available_units")).thenReturn(10L);

        unitAvailabilityCacheService.decreaseAvailableUnits();

        verify(valueOperations).set("available_units", 9L);
    }

    @Test
    void shouldGetAvailableUnits() {
        when(valueOperations.get("available_units")).thenReturn(10L);

        Long availableUnits = unitAvailabilityCacheService.getAvailableUnits();

        assertEquals(10L, availableUnits);
    }
}
