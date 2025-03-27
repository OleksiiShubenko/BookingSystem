package com.booking.controller;

import com.booking.dataModel.Unit;
import com.booking.dataModel.UnitType;
import com.booking.dataModel.dto.UnitAvailabilityInfo;
import com.booking.dataModel.dto.UnitDto;
import com.booking.dataModel.dto.UnitSearchParams;
import com.booking.service.UnitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitControllerTest {

    @Mock
    private UnitService unitService;

    @InjectMocks
    private UnitController unitController;

    @Test
    void createUnit_ShouldReturnCreatedUnit() {
        UnitDto unitDto = new UnitDto("user1", 2, UnitType.APARTMENTS, 3, 1200.50, "Spacious unit");
        Unit expectedUnit = new Unit();
        when(unitService.createUnit(unitDto)).thenReturn(unitDto);

        UnitDto actualUnit = unitController.createUnit(unitDto);

        assertNotNull(actualUnit);
        assertEquals(unitDto, actualUnit);
        verify(unitService, times(1)).createUnit(unitDto);
    }

    @Test
    void searchUnits_ShouldReturnListOfUnits() {
        UnitSearchParams params = new UnitSearchParams();
        List<UnitDto> expectedUnits = List.of(new UnitDto("user1", 2, UnitType.HOME, 3, 1500.0, "Nice unit"));
        when(unitService.getUnits(params, 0, 10)).thenReturn(expectedUnits);

        List<UnitDto> result = unitController.searchUnits(params, 0, 10);

        assertEquals(expectedUnits, result);
        verify(unitService, times(1)).getUnits(params, 0, 10);
    }

    @Test
    void getUnitAvailabilityInfo_ShouldReturnAvailabilityInfo() {
        UnitAvailabilityInfo expectedInfo = new UnitAvailabilityInfo(10L, "10 units are available");
        when(unitService.getUnitAvailabilityInfo()).thenReturn(expectedInfo);

        UnitAvailabilityInfo result = unitController.getUnitAvailabilityInfo();

        assertEquals(expectedInfo, result);
        verify(unitService, times(1)).getUnitAvailabilityInfo();
    }

}

