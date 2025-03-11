package com.booking.repository;

import com.booking.dataModel.Event;
import com.booking.dataModel.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
}
