package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.event.Event;
import com.tastyhouse.core.entity.event.EventPrize;
import com.tastyhouse.core.entity.event.EventStatus;
import com.tastyhouse.core.repository.event.EventJpaRepository;
import com.tastyhouse.core.repository.event.EventPrizeJpaRepository;
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

    public Optional<Event> getActiveEvent() {
        return eventJpaRepository.findFirstByStatusOrderByStartAtDesc(EventStatus.ACTIVE);
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
}
