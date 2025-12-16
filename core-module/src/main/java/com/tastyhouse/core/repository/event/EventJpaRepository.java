package com.tastyhouse.core.repository.event;

import com.tastyhouse.core.entity.event.Event;
import com.tastyhouse.core.entity.event.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventJpaRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatusOrderByStartAtDesc(EventStatus status);

    Optional<Event> findFirstByStatusOrderByStartAtDesc(EventStatus status);
}
