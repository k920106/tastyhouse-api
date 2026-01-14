package com.tastyhouse.webapi.place.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PlaceReviewListItem {
    private final Long id;
    private final List<String> imageUrls;
    private final Double totalRating;
    private final String content;
    private final String memberNickname;
    private final String memberProfileImageUrl;
    private final LocalDateTime createdAt;
    private final Long productId;
    private final String productName;
}
