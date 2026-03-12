package com.tastyhouse.webapi.review.response;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewDetailResponse(
        Long id,
        Long placeId,
        String placeName,
        String stationName,
        String content,
        Double totalRating,
        Double tasteRating,
        Double amountRating,
        Double priceRating,
        Double atmosphereRating,
        Double kindnessRating,
        Double hygieneRating,
        Boolean willRevisit,
        Long memberId,
        String memberNickname,
        String memberProfileImageUrl,
        LocalDateTime createdAt,
        List<String> imageUrls,
        List<String> tagNames
) {
}
