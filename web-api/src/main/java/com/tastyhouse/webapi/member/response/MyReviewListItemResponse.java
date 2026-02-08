package com.tastyhouse.webapi.member.response;

import com.tastyhouse.core.entity.review.dto.MyReviewListItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MyReviewListItemResponse {
    private Long id;
    private String imageUrl;

    public static MyReviewListItemResponse from(MyReviewListItemDto dto) {
        return MyReviewListItemResponse.builder()
            .id(dto.getId())
            .imageUrl(dto.getImageUrl())
            .build();
    }
}
