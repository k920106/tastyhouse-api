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
    private final String content;
    private final Long memberId;
    private final String memberNickname;
    private final String memberProfileImageUrl;
    private final LocalDateTime createdAt;
    private final Long productId;
    private final String productName;
    private Long likeCount;
    private Long commentCount;

    @QueryProjection
    public LatestReviewListItemDto(Long id, String stationName,
                                   Double totalRating, String content,
                                   Long memberId, String memberNickname, String memberProfileImageUrl,
                                   LocalDateTime createdAt, Long productId, String productName) {
        this.id = id;
        this.imageUrls = new ArrayList<>();
        this.stationName = stationName;
        this.totalRating = totalRating;
        this.content = content;
        this.memberId = memberId;
        this.memberNickname = memberNickname;
        this.memberProfileImageUrl = memberProfileImageUrl;
        this.createdAt = createdAt;
        this.productId = productId;
        this.productName = productName;
        this.likeCount = 0L;
        this.commentCount = 0L;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }
}
