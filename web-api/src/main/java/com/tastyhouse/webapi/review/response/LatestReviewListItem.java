package com.tastyhouse.webapi.review.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class LatestReviewListItem {
    private final Long id;
    private final List<String> imageUrls;
    private final String stationName;
    private final Double totalRating;
    private final String content;
    private final Long memberId;
    private final String memberNickname;
    private final String memberProfileImageUrl;
    private final LocalDateTime createdAt;
}
