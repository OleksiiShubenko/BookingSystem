package com.booking.repository;

import com.booking.dataModel.Unit;
import com.booking.dataModel.UnitType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Integer> {

    @Query("SELECT u FROM Unit u WHERE " +
            "(:minCost IS NULL OR u.cost >= :minCost) AND " +
            "(:maxCost IS NULL OR u.cost <= :maxCost) AND " +
            "(:numRooms IS NULL OR u.numRooms = :numRooms) AND " +
            "(:floor IS NULL OR u.floor = :floor) AND "+
            "(:type IS NULL OR u.type = :type)"
    )
    Page<Unit> findUnitsByCriteria(
            @Param("minCost") Double minCost,
            @Param("maxCost") Double maxCost,
            @Param("floor") Integer floor,
            @Param("numRooms") Integer numRooms,
            @Param("type") UnitType type,
            Pageable pageable
    );

}
