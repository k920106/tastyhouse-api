package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.event.Event;
import com.tastyhouse.core.entity.event.EventAnnouncement;
import com.tastyhouse.core.entity.event.EventPrize;
import com.tastyhouse.core.entity.event.EventStatus;
import com.tastyhouse.core.entity.event.EventType;
import com.tastyhouse.core.entity.event.EventWinner;
import com.tastyhouse.core.repository.event.EventAnnouncementJpaRepository;
import com.tastyhouse.core.repository.event.EventJpaRepository;
import com.tastyhouse.core.repository.event.EventPrizeJpaRepository;
import com.tastyhouse.core.repository.event.EventWinnerJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventCoreService {

    private final EventJpaRepository eventJpaRepository;
    private final EventPrizeJpaRepository eventPrizeJpaRepository;
    private final EventWinnerJpaRepository eventWinnerJpaRepository;
    private final EventAnnouncementJpaRepository eventAnnouncementJpaRepository;

    public Optional<Event> getActiveRankingEvent() {
        return eventJpaRepository.findFirstByStatusAndTypeOrderByStartAtDesc(EventStatus.ACTIVE, EventType.RANKING);
    }

    public List<Event> getEventsByStatus(EventStatus status) {
        return eventJpaRepository.findByStatusOrderByStartAtDesc(status);
    }

    public List<EventPrize> getEventPrizes(Long eventId) {
        return eventPrizeJpaRepository.findByEventIdOrderByPrizeRankAsc(eventId);
    }

    @Transactional
    public Event saveEvent(Event event) {
        return eventJpaRepository.save(event);
    }

    @Transactional
    public EventPrize saveEventPrize(EventPrize eventPrize) {
        return eventPrizeJpaRepository.save(eventPrize);
    }

    public Optional<Event> findEventById(Long eventId) {
        return eventJpaRepository.findById(eventId);
    }

    public List<EventWinner> findEventWinnersByEventId(Long eventId) {
        return eventWinnerJpaRepository.findByEventIdOrderByAnnouncedAtDescRankNoAsc(eventId);
    }

    public List<EventWinner> findAllEventWinners() {
        return eventWinnerJpaRepository.findAllByOrderByAnnouncedAtDescRankNoAsc();
    }

    @Transactional
    public EventWinner saveEventWinner(EventWinner eventWinner) {
        return eventWinnerJpaRepository.save(eventWinner);
    }

    public List<EventAnnouncement> findAllEventAnnouncements() {
        return eventAnnouncementJpaRepository.findAllByOrderByAnnouncedAtDesc();
    }

    public Optional<EventAnnouncement> findEventAnnouncementByEventId(Long eventId) {
        return eventAnnouncementJpaRepository.findByEventId(eventId);
    }

    @Transactional
    public EventAnnouncement saveEventAnnouncement(EventAnnouncement eventAnnouncement) {
        return eventAnnouncementJpaRepository.save(eventAnnouncement);
    }
}
