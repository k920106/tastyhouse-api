package com.tastyhouse.core.repository.event;

import com.tastyhouse.core.entity.event.EventWinner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventWinnerJpaRepository extends JpaRepository<EventWinner, Long> {

    List<EventWinner> findByEventIdOrderByAnnouncedAtDescRankNoAsc(Long eventId);

    List<EventWinner> findAllByOrderByAnnouncedAtDescRankNoAsc();
}
