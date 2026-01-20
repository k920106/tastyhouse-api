package com.tastyhouse.webapi.event;

import com.tastyhouse.core.entity.event.EventPrize;
import com.tastyhouse.core.service.EventCoreService;
import com.tastyhouse.webapi.event.response.EventDurationResponse;
import com.tastyhouse.webapi.event.response.PrizeItem;
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
            .imageUrl(eventPrize.getImageUrl())
            .build();
    }
}
