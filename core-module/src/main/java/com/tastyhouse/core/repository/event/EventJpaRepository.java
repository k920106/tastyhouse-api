package com.tastyhouse.core.repository.event;

import com.tastyhouse.core.entity.event.Event;
import com.tastyhouse.core.entity.event.EventStatus;
import com.tastyhouse.core.entity.event.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventJpaRepository extends JpaRepository<Event, Long> {

    @Query("""
        SELECT e FROM Event e
        WHERE e.status = :status
        ORDER BY e.startAt DESC
        """)
    List<Event> findByStatusOrderByStartAtDesc(@Param("status") EventStatus status);

    @Query("""
        SELECT e FROM Event e
        WHERE e.status = :status
        ORDER BY e.startAt DESC
        LIMIT 1
        """)
    Optional<Event> findFirstByStatusOrderByStartAtDesc(@Param("status") EventStatus status);

    @Query("""
        SELECT e FROM Event e
        WHERE e.status = :status AND e.type = :type
        ORDER BY e.startAt DESC
        LIMIT 1
        """)
    Optional<Event> findFirstByStatusAndTypeOrderByStartAtDesc(@Param("status") EventStatus status, @Param("type") EventType type);
}
