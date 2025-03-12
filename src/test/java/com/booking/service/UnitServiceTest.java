package com.booking.service;

import com.booking.dataModel.Booking;
import com.booking.dataModel.Unit;
import com.booking.dataModel.UnitType;
import com.booking.dataModel.User;
import com.booking.dataModel.dto.UnitAvailabilityInfo;
import com.booking.dataModel.dto.UnitDto;
import com.booking.dataModel.dto.UnitSearchParams;
import com.booking.dataModel.dto.UnitSort;
import com.booking.dataModel.exceptions.EntityNotFoundException;
import com.booking.repository.UnitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepositoryMock;
    @Mock
    private UserService userService;
    @Mock
    private BookingService bookingServiceMock;

    @Mock
    private UnitAvailabilityCacheService unitAvailabilityCacheService;

    @InjectMocks
    private UnitService unitService;

    private final User USER = User.builder().username("user1").password("pass1").build();
    private final Unit unit = new Unit(1, 3, UnitType.APARTMENTS, 2, 1500.0, "Apartments", null, USER);
    private final UnitDto unitDto = new UnitDto("user1", 3, UnitType.APARTMENTS, 2, 1500.0, "Apartments");

    @Test
    void getUnit_ShouldReturnUnit_WhenUnitExists() {
        when(unitRepositoryMock.findById(1)).thenReturn(Optional.of(unit));

        Unit result = unitService.getUnit(1);

        assertEquals(unit, result);

        verify(unitRepositoryMock, times(1)).findById(1);
    }

    @Test
    void getUnit_ShouldThrowException_WhenUnitDoesNotExist() {
        when(unitRepositoryMock.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> unitService.getUnit(1));

        assertEquals("Unit with id: 1 does not exist", exception.getMessage());

        verify(unitRepositoryMock, times(1)).findById(1);
    }

    @Test
    void createUnit_ShouldReturnCreatedUnit_WhenUnitIsCreated() {
        when(unitRepositoryMock.save(any(Unit.class))).thenReturn(unit);
        when(userService.getUser(unitDto.username())).thenReturn(USER);

        Unit result = unitService.createUnit(unitDto);

        assertEquals(unit, result);

        verify(unitRepositoryMock, times(1)).save(any(Unit.class));
        verify(unitAvailabilityCacheService, times(1)).increaseAvailableUnits();
    }

    @Test
    void countAll_ShouldReturnCorrectCount() {
        when(unitRepositoryMock.count()).thenReturn(5L);

        long count = unitService.countAll();

        assertEquals(5L, count);
        verify(unitRepositoryMock, times(1)).count();
    }

    @Test
    void testGetUnits_NoOverlap() {
        var params = UnitSearchParams.builder()
                .minCost(50.0)
                .maxCost(200.0)
                .floor(2)
                .numRooms(3)
                .type(UnitType.APARTMENTS)
                .sortBy(UnitSort.COST)
                .sortOrder(Sort.Direction.ASC)
                .build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "cost"));
        Page<Unit> page = mock(Page.class);
        when(unitRepositoryMock.findUnitsByCriteria(50.0, 200.0, 2, 3, UnitType.APARTMENTS, pageable))
                .thenReturn(page);
        when(page.getContent()).thenReturn(List.of(unit));

        List<UnitDto> result = unitService.getUnits(params, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(unitRepositoryMock, times(1)).findUnitsByCriteria(50.0, 200.0, 2, 3, UnitType.APARTMENTS, pageable);
    }

    @Test
    void testGetUnits_WithOverlap() {
        Instant fromTime = Instant.parse("2025-03-01T10:00:00Z");
        Instant toTime = Instant.parse("2025-03-01T12:00:00Z");
        var params = UnitSearchParams.builder()
                .minCost(50.0)
                .maxCost(200.0)
                .floor(2)
                .numRooms(3)
                .type(UnitType.APARTMENTS)
                .fromTime(fromTime)
                .toTime(toTime)
                .sortBy(UnitSort.COST)
                .sortOrder(Sort.Direction.ASC)
                .build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "cost"));
        Page<Unit> page = mock(Page.class);

        when(unitRepositoryMock.findUnitsByCriteria(50.0, 200.0, 2, 3, UnitType.APARTMENTS, pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(List.of(unit));
        when(bookingServiceMock.findAllOverlappedBookings(List.of(1), fromTime, toTime)).thenReturn(List.of(Booking.builder().unit(Unit.builder().id(1).build()).build()));

        List<UnitDto> result = unitService.getUnits(params, 0, 10);

        assertTrue(result.isEmpty());
        verify(unitRepositoryMock, times(1)).findUnitsByCriteria(50.0, 200.0, 2, 3, UnitType.APARTMENTS, pageable);
        verify(bookingServiceMock, times(1)).findAllOverlappedBookings(List.of(1), fromTime, toTime);
    }

    @Test
    void testGetUnitAvailabilityInfo() {
        when(unitAvailabilityCacheService.getAvailableUnits()).thenReturn(100L);

        UnitAvailabilityInfo result = unitService.getUnitAvailabilityInfo();

        assertNotNull(result);
        assertEquals(100, result.availableAmount());
        assertEquals("Units availability info", result.description());
        verify(unitAvailabilityCacheService, times(1)).getAvailableUnits();
    }
}
