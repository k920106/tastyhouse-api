package com.tastyhouse.core.repository.event;

import com.tastyhouse.core.entity.event.EventAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventAnnouncementJpaRepository extends JpaRepository<EventAnnouncement, Long> {

    Optional<EventAnnouncement> findByEventId(Long eventId);

    List<EventAnnouncement> findAllByOrderByAnnouncedAtDesc();
}
