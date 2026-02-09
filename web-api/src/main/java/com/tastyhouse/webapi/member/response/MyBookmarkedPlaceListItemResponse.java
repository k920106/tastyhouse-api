package com.tastyhouse.webapi.member.response;

import com.tastyhouse.core.entity.place.dto.MyBookmarkedPlaceItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MyBookmarkedPlaceListItemResponse {
    private Long placeId;
    private Long bookmarkId;
    private String placeName;
    private String stationName;
    private Double rating;
    private String imageUrl;
    private Boolean isBookmarked;

    public static MyBookmarkedPlaceListItemResponse from(MyBookmarkedPlaceItemDto dto) {
        return MyBookmarkedPlaceListItemResponse.builder()
            .placeId(dto.getPlaceId())
            .bookmarkId(dto.getBookmarkId())
            .placeName(dto.getPlaceName())
            .stationName(dto.getStationName())
            .rating(dto.getRating())
            .imageUrl(dto.getImageUrl())
            .isBookmarked(dto.getIsBookmarked())
            .build();
    }
}
