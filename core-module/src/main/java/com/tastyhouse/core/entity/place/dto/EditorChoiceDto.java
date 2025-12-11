package com.tastyhouse.core.entity.place.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.tastyhouse.core.entity.product.dto.ProductSimpleDto;
import lombok.Getter;

import java.util.List;

@Getter
public class EditorChoiceDto {
    private final Long id;
    private final Long placeId;
    private final String placeName;
    private final String title;
    private final String content;
    private final String placeImageUrl;
    private final List<ProductSimpleDto> products;

    @QueryProjection
    public EditorChoiceDto(Long id, Long placeId, String placeName, String title, String content, String placeImageUrl, List<ProductSimpleDto> products) {
        this.id = id;
        this.placeId = placeId;
        this.placeName = placeName;
        this.title = title;
        this.content = content;
        this.placeImageUrl = placeImageUrl;
        this.products = products;
    }
}
