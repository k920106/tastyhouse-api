package com.tastyhouse.core.entity.place;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlaceImageCategory {

    EXTERIOR("가게 외관"),
    INTERIOR("가게 내부"),
    FOOD("음식"),
    OTHER("기타");

    private final String description;
}
