package com.tastyhouse.core.repository.event;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.event.Event;
import com.tastyhouse.core.entity.event.EventAnnouncement;
import com.tastyhouse.core.entity.event.EventPrize;
import com.tastyhouse.core.entity.event.EventStatus;
import com.tastyhouse.core.entity.event.EventType;
import com.tastyhouse.core.entity.event.EventWinner;
import com.tastyhouse.core.entity.event.QEvent;
import com.tastyhouse.core.entity.event.QEventAnnouncement;
import com.tastyhouse.core.entity.event.QEventPrize;
import com.tastyhouse.core.entity.event.QEventWinner;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Event> findByStatusOrderByStartAtDesc(EventStatus status, Pageable pageable) {
        QEvent event = QEvent.event;

        List<Event> content = queryFactory
            .selectFrom(event)
            .where(event.status.eq(status))
            .orderBy(event.startAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .select(event.count())
            .from(event)
            .where(event.status.eq(status))
            .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Optional<Event> findLatestByStatus(EventStatus status) {
        QEvent event = QEvent.event;

        Event result = queryFactory
            .selectFrom(event)
            .where(event.status.eq(status))
            .orderBy(event.startAt.desc())
            .limit(1)
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Event> findLatestByStatusAndType(EventStatus status, EventType type) {
        QEvent event = QEvent.event;

        Event result = queryFactory
            .selectFrom(event)
            .where(
                event.status.eq(status),
                event.type.eq(type)
            )
            .orderBy(event.startAt.desc())
            .limit(1)
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<EventPrize> findPrizesByEventIdOrderByPrizeRankAsc(Long eventId) {
        QEventPrize eventPrize = QEventPrize.eventPrize;

        return queryFactory
            .selectFrom(eventPrize)
            .where(eventPrize.eventId.eq(eventId))
            .orderBy(eventPrize.prizeRank.asc())
            .fetch();
    }

    @Override
    public List<EventWinner> findWinnersByEventIdOrderByAnnouncedAtDescRankNoAsc(Long eventId) {
        QEventWinner eventWinner = QEventWinner.eventWinner;

        return queryFactory
            .selectFrom(eventWinner)
            .where(eventWinner.eventId.eq(eventId))
            .orderBy(
                eventWinner.announcedAt.desc(),
                eventWinner.rankNo.asc()
            )
            .fetch();
    }

    @Override
    public List<EventWinner> findAllWinnersOrderByAnnouncedAtDescRankNoAsc() {
        QEventWinner eventWinner = QEventWinner.eventWinner;

        return queryFactory
            .selectFrom(eventWinner)
            .orderBy(
                eventWinner.announcedAt.desc(),
                eventWinner.rankNo.asc()
            )
            .fetch();
    }

    @Override
    public Optional<EventAnnouncement> findAnnouncementByEventId(Long eventId) {
        QEventAnnouncement eventAnnouncement = QEventAnnouncement.eventAnnouncement;

        EventAnnouncement result = queryFactory
            .selectFrom(eventAnnouncement)
            .where(eventAnnouncement.eventId.eq(eventId))
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<EventAnnouncement> findAllAnnouncementsOrderByAnnouncedAtDesc(Pageable pageable) {
        QEventAnnouncement eventAnnouncement = QEventAnnouncement.eventAnnouncement;

        List<EventAnnouncement> content = queryFactory
            .selectFrom(eventAnnouncement)
            .orderBy(eventAnnouncement.announcedAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .select(eventAnnouncement.count())
            .from(eventAnnouncement)
            .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
