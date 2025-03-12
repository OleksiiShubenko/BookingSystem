package com.booking.service;

import com.booking.dataModel.Unit;
import com.booking.dataModel.dto.UnitAvailabilityInfo;
import com.booking.dataModel.dto.UnitDto;
import com.booking.dataModel.dto.UnitSearchParams;
import com.booking.dataModel.exceptions.EntityNotFoundException;
import com.booking.repository.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitService {

    private final UnitRepository unitRepository;
    private final UserService userService;
    private final BookingService bookingService;
    private final UnitAvailabilityCacheService unitAvailabilityCacheService;

    @Autowired
    public UnitService(UnitRepository unitRepository,
                       UserService userService,
                       @Lazy UnitAvailabilityCacheService unitAvailabilityCacheService,
                       @Lazy BookingService bookingService) {
        this.unitRepository = unitRepository;
        this.userService = userService;
        this.unitAvailabilityCacheService = unitAvailabilityCacheService;
        this.bookingService = bookingService;
    }

    /**
     * Returns Unit by provided id
     */
    public Unit getUnit(Integer unitId) {
        var unit = unitRepository.findById(unitId);
        if (unit.isEmpty()) {
            throw new EntityNotFoundException("Unit with id: " + unitId + " does not exist");
        }
        return unit.get();
    }

    /**
     * Create a new unit by provided parameters
     */
    public Unit createUnit(UnitDto unitDto) {
        var unit = Unit.builder()
                .numRooms(unitDto.numRooms())
                .type(unitDto.type())
                .floor(unitDto.floor())
                .cost(unitDto.cost())
                .description(unitDto.description())
                .owner(userService.getUser(unitDto.username()))
                .build();

        unitAvailabilityCacheService.increaseAvailableUnits();
        return unitRepository.save(unit);
    }

    public long countAll() {
        return unitRepository.count();
    }

    /**
     * Returns a list of units according to provided search parameters
     */
    public List<UnitDto> getUnits(UnitSearchParams params, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(params.getSortOrder() == null ? Sort.Direction.ASC : params.getSortOrder(), params.getSortBy() == null ? "cost" : params.getSortBy().getValue())
        );

        List<Unit> units = unitRepository.findUnitsByCriteria(
                params.getMinCost(),
                params.getMaxCost(),
                params.getFloor(),
                params.getNumRooms(),
                params.getType(),
                pageable
        ).getContent();

        // if times are provided and some units are overlapped, filter them out
        if (params.getFromTime() != null && params.getToTime() != null) {
            var ids = units.stream().map(Unit::getId).toList();
            var allOverlappedBookingUnitIds = bookingService.findAllOverlappedBookings(ids, params.getFromTime(), params.getToTime()).stream()
                    .map(it -> it.getUnit().getId())
                    .toList();

            units = units.stream().filter(it -> !allOverlappedBookingUnitIds.contains(it.getId())).toList();
        }

        return units.stream().map(it -> new UnitDto(
                it.getOwner().getUsername(),
                it.getNumRooms(),
                it.getType(),
                it.getFloor(),
                it.getCost(),
                it.getDescription()
        )).toList();
    }

    /**
     * Returns the Unit availability info with amount of available units
     */
    public UnitAvailabilityInfo getUnitAvailabilityInfo() {
        return new UnitAvailabilityInfo(
                unitAvailabilityCacheService.getAvailableUnits(),
                "Units availability info"
        );
    }
}
