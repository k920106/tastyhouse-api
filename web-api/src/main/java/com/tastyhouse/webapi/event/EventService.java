package com.tastyhouse.webapi.event;

import com.tastyhouse.core.common.PageResult;
import com.tastyhouse.core.entity.event.Event;
import com.tastyhouse.core.entity.event.EventAnnouncement;
import com.tastyhouse.core.entity.event.EventPrize;
import com.tastyhouse.core.entity.event.EventStatus;
import com.tastyhouse.core.service.EventCoreService;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.event.response.*;
import com.tastyhouse.core.exception.EntityNotFoundException;
import com.tastyhouse.core.exception.ErrorCode;
import com.tastyhouse.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    public PageResult<EventListItemResponse> getEventList(EventStatus status, PageRequest pageRequest) {
        return eventCoreService.getEventsByStatus(status, pageRequest.getPage(), pageRequest.getSize())
            .map(this::convertToEventListItemResponse);
    }

    public EventDetailResponse getEventDetail(Long eventId) {
        Event event = eventCoreService.findEventById(eventId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND, "이벤트를 찾을 수 없습니다."));

        return convertToEventDetailResponse(event);
    }

    public PageResult<EventAnnouncementListItemResponse> getEventAnnouncementList(PageRequest pageRequest) {
        return eventCoreService.findAllEventAnnouncements(pageRequest.getPage(), pageRequest.getSize())
            .map(this::convertToEventAnnouncementListItemResponse);
    }

    private EventListItemResponse convertToEventListItemResponse(Event event) {
        return EventListItemResponse.builder()
            .id(event.getId())
            .name(event.getName())
            .thumbnailImageUrl(fileService.getFileUrl(event.getThumbnailImageFileId()))
            .startAt(event.getStartAt())
            .endAt(event.getEndAt())
            .build();
    }

    private EventDetailResponse convertToEventDetailResponse(Event event) {
        return EventDetailResponse.builder()
            .bannerImageUrl(fileService.getFileUrl(event.getBannerImageFileId()))
            .build();
    }

    private EventAnnouncementListItemResponse convertToEventAnnouncementListItemResponse(EventAnnouncement announcement) {
        return EventAnnouncementListItemResponse.builder()
            .id(announcement.getId())
            .name(announcement.getName())
            .content(announcement.getContent())
            .announcedAt(announcement.getAnnouncedAt())
            .build();
    }
}
