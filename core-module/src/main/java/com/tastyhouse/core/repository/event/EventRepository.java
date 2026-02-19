package com.tastyhouse.core.repository.event;

import com.tastyhouse.core.entity.event.Event;
import com.tastyhouse.core.entity.event.EventAnnouncement;
import com.tastyhouse.core.entity.event.EventPrize;
import com.tastyhouse.core.entity.event.EventStatus;
import com.tastyhouse.core.entity.event.EventType;
import com.tastyhouse.core.entity.event.EventWinner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EventRepository {

    Page<Event> findByStatusOrderByStartAtDesc(EventStatus status, Pageable pageable);

    Optional<Event> findLatestByStatus(EventStatus status);

    Optional<Event> findLatestByStatusAndType(EventStatus status, EventType type);

    List<EventPrize> findPrizesByEventIdOrderByPrizeRankAsc(Long eventId);

    List<EventWinner> findWinnersByEventIdOrderByAnnouncedAtDescRankNoAsc(Long eventId);

    List<EventWinner> findAllWinnersOrderByAnnouncedAtDescRankNoAsc();

    Optional<EventAnnouncement> findAnnouncementByEventId(Long eventId);

    Page<EventAnnouncement> findAllAnnouncementsOrderByAnnouncedAtDesc(Pageable pageable);
}
