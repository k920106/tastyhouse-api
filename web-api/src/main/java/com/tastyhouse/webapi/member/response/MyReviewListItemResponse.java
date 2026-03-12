package com.tastyhouse.webapi.member.response;

import com.tastyhouse.core.entity.review.dto.MyReviewListItemDto;

public record MyReviewListItemResponse(
        Long id,
        String imageUrl
) {
    public static MyReviewListItemResponse from(MyReviewListItemDto dto) {
        return new MyReviewListItemResponse(dto.id(), dto.imageUrl());
    }
}
