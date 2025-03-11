package com.booking.repository;

import com.booking.dataModel.Booking;
import com.booking.dataModel.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b JOIN FETCH b.unit WHERE b.id = :id")
    Booking findByIdWithUnit(@Param("id") Integer id);

    @Query("SELECT b FROM Booking b WHERE b.unit.id = :unitId AND b.status = :status " +
            "AND ((:fromTime BETWEEN b.fromTime AND b.toTime) " +
            "OR (:toTime BETWEEN b.fromTime AND b.toTime) " +
            "OR (b.fromTime BETWEEN :fromTime AND :toTime) " +
            "OR (b.toTime BETWEEN :fromTime AND :toTime))")
    List<Booking> findAllOverlappedBookings(@Param("unitId") Integer unitId, @Param("fromTime") Instant fromTime, @Param("toTime") Instant toTime, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.unit.id IN :unitIds AND b.status = :status " +
            "AND ((:fromTime BETWEEN b.fromTime AND b.toTime) " +
            "OR (:toTime BETWEEN b.fromTime AND b.toTime) " +
            "OR (b.fromTime BETWEEN :fromTime AND :toTime) " +
            "OR (b.toTime BETWEEN :fromTime AND :toTime))")
    List<Booking> findAllOverlappedBookings(
            @Param("unitIds") List<Integer> unitIds,
            @Param("fromTime") Instant fromTime,
            @Param("toTime") Instant toTime,
            @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN b.payment p " +
            "WHERE b.user.username = :username AND " +
            "b.unit.id = :unitId AND " +
            "b.fromTime = :fromTime AND " +
            "b.toTime = :toTime AND " +
            "b.status = :status")
    Booking findByUserNameAndUnitIdAndFromTimeAndToTimeAndStatus(String username, Integer unitId, Instant fromTime, Instant toTime, BookingStatus status);
}
