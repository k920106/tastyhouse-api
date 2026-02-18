package com.tastyhouse.webapi.event;

import com.tastyhouse.core.entity.event.Event;
import com.tastyhouse.core.entity.event.EventPrize;
import com.tastyhouse.core.entity.event.EventStatus;
import com.tastyhouse.core.entity.event.EventWinner;
import com.tastyhouse.core.service.EventCoreService;
import com.tastyhouse.webapi.event.response.*;
import com.tastyhouse.webapi.exception.NotFoundException;
import com.tastyhouse.webapi.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventCoreService eventCoreService;
    private final FileService fileService;

    public Optional<EventDurationResponse> getRankingEventDuration() {
        return eventCoreService.getActiveRankingEvent()
                .map(event -> EventDurationResponse.builder()
                        .startAt(event.getStartAt())
                        .endAt(event.getEndAt())
                        .build());
    }

    public List<PrizeItem> getActivePrizes() {
        return eventCoreService.getActiveRankingEvent()
            .map(event -> {
                List<EventPrize> eventPrizes = eventCoreService.getEventPrizes(event.getId());
                return eventPrizes.stream()
                    .map(this::convertToPrizeItem)
                    .toList();
            })
            .orElse(Collections.emptyList());
    }

    private PrizeItem convertToPrizeItem(EventPrize eventPrize) {
        return PrizeItem.builder()
            .id(eventPrize.getId())
            .prizeRank(eventPrize.getPrizeRank())
            .name(eventPrize.getName())
            .brand(eventPrize.getBrand())
            .imageUrl(fileService.getFileUrl(eventPrize.getImageFileId()))
            .build();
    }

    public List<EventListItemResponse> getEventList(EventStatus status) {
        List<Event> events = eventCoreService.getEventsByStatus(status);
        return events.stream()
            .map(this::convertToEventListItemResponse)
            .toList();
    }

    public EventDetailResponse getEventDetail(Long eventId) {
        Event event = eventCoreService.findEventById(eventId)
            .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));

        return convertToEventDetailResponse(event);
    }

    public List<EventWinnerListResponse> getAllEventWinners() {
        List<EventWinner> winners = eventCoreService.findAllEventWinners();

        // 이벤트 ID별로 그룹화
        Map<Long, List<EventWinner>> winnersByEvent = winners.stream()
            .collect(Collectors.groupingBy(EventWinner::getEventId));

        return winnersByEvent.entrySet().stream()
            .map(entry -> {
                Long eventId = entry.getKey();
                List<EventWinner> eventWinners = entry.getValue();

                Event event = eventCoreService.findEventById(eventId).orElse(null);
                String eventName = event != null ? event.getName() + " 당첨자 발표" : "이벤트 당첨자 발표";
                LocalDateTime announcedAt = eventWinners.isEmpty() ? null : eventWinners.get(0).getAnnouncedAt();

                return EventWinnerListResponse.builder()
                    .eventId(eventId)
                    .eventName(eventName)
                    .announcedAt(announcedAt)
                    .winners(eventWinners.stream()
                        .map(this::convertToEventWinnerItemResponse)
                        .toList())
                    .build();
            })
            .sorted(Comparator.comparing(EventWinnerListResponse::getAnnouncedAt,
                Comparator.nullsLast(Comparator.reverseOrder())))
            .toList();
    }

    public EventWinnerListResponse getEventWinners(Long eventId) {
        Event event = eventCoreService.findEventById(eventId)
            .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));

        List<EventWinner> winners = eventCoreService.findEventWinnersByEventId(eventId);
        LocalDateTime announcedAt = winners.isEmpty() ? null : winners.get(0).getAnnouncedAt();

        return EventWinnerListResponse.builder()
            .eventId(eventId)
            .eventName(event.getName() + " 당첨자 발표")
            .announcedAt(announcedAt)
            .winners(winners.stream()
                .map(this::convertToEventWinnerItemResponse)
                .toList())
            .build();
    }

    private EventListItemResponse convertToEventListItemResponse(Event event) {
        return EventListItemResponse.builder()
            .id(event.getId())
            .name(event.getName())
            .description(event.getDescription())
            .subtitle(event.getSubtitle())
            .thumbnailImageUrl(fileService.getFileUrl(event.getThumbnailImageFileId()))
            .type(event.getType().name())
            .status(event.getStatus().name())
            .startAt(event.getStartAt())
            .endAt(event.getEndAt())
            .build();
    }

    private EventDetailResponse convertToEventDetailResponse(Event event) {
        return EventDetailResponse.builder()
            .id(event.getId())
            .name(event.getName())
            .description(event.getDescription())
            .subtitle(event.getSubtitle())
            .thumbnailImageUrl(fileService.getFileUrl(event.getThumbnailImageFileId()))
            .bannerImageUrl(fileService.getFileUrl(event.getBannerImageFileId()))
            .contentHtml(event.getContentHtml())
            .type(event.getType().name())
            .status(event.getStatus().name())
            .startAt(event.getStartAt())
            .endAt(event.getEndAt())
            .build();
    }

    private EventWinnerItemResponse convertToEventWinnerItemResponse(EventWinner winner) {
        return EventWinnerItemResponse.builder()
            .id(winner.getId())
            .eventId(winner.getEventId())
            .rankNo(winner.getRankNo())
            .winnerName(winner.getMaskedName())
            .phoneNumber(winner.getMaskedPhoneNumber())
            .announcedAt(winner.getAnnouncedAt())
            .build();
    }
}
