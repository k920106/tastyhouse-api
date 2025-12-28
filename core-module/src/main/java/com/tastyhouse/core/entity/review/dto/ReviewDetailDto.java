package com.tastyhouse.core.entity.review.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ReviewDetailDto {
    private final Long id;
    private final Long placeId;
    private final String placeName;
    private final String stationName;
    private final String content;
    private final Double totalRating;
    private final Double tasteRating;
    private final Double amountRating;
    private final Double priceRating;
    private final Double atmosphereRating;
    private final Double kindnessRating;
    private final Double hygieneRating;
    private final Boolean willRevisit;
    private final Long memberId;
    private final String memberNickname;
    private final String memberProfileImageUrl;
    private final LocalDateTime createdAt;
    private List<String> imageUrls;
    private List<String> tagNames;
    private Boolean isLiked;

    @QueryProjection
    public ReviewDetailDto(Long id, Long placeId, String placeName, String stationName, String content, Double totalRating, Double tasteRating, Double amountRating, Double priceRating, Double atmosphereRating, Double kindnessRating, Double hygieneRating, Boolean willRevisit, Long memberId, String memberNickname, String memberProfileImageUrl, LocalDateTime createdAt) {
        this.id = id;
        this.placeId = placeId;
        this.placeName = placeName;
        this.stationName = stationName;
        this.content = content;
        this.totalRating = totalRating;
        this.tasteRating = tasteRating;
        this.amountRating = amountRating;
        this.priceRating = priceRating;
        this.atmosphereRating = atmosphereRating;
        this.kindnessRating = kindnessRating;
        this.hygieneRating = hygieneRating;
        this.willRevisit = willRevisit;
        this.memberId = memberId;
        this.memberNickname = memberNickname;
        this.memberProfileImageUrl = memberProfileImageUrl;
        this.createdAt = createdAt;
        this.imageUrls = new ArrayList<>();
        this.tagNames = new ArrayList<>();
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }
}
