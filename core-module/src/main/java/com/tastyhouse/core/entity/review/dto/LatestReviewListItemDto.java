package com.tastyhouse.core.entity.review.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class LatestReviewListItemDto {
    private final Long id;
    private List<String> imageUrls;
    private final String stationName;
    private final Double totalRating;
    private final String title;
    private final String content;
    private final Long memberId;
    private final String memberNickname;
    private final String memberProfileImageUrl;
    private final LocalDateTime createdAt;

    @QueryProjection
    public LatestReviewListItemDto(Long id, String stationName,
                                   Double totalRating, String title, String content,
                                   Long memberId, String memberNickname, String memberProfileImageUrl,
                                   LocalDateTime createdAt) {
        this.id = id;
        this.imageUrls = new ArrayList<>();
        this.stationName = stationName;
        this.totalRating = totalRating;
        this.title = title;
        this.content = content;
        this.memberId = memberId;
        this.memberNickname = memberNickname;
        this.memberProfileImageUrl = memberProfileImageUrl;
        this.createdAt = createdAt;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
