package com.tastyhouse.core.repository.event;

import com.tastyhouse.core.entity.event.EventPrize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventPrizeJpaRepository extends JpaRepository<EventPrize, Long> {

    List<EventPrize> findByEventIdOrderByPrizeRankAsc(Long eventId);
}
