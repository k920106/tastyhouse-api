package com.tastyhouse.core.entity.place;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DayType {

    WEEKDAY("평일"),
    SATURDAY("토요일"),
    SUNDAY("일요일"),
    HOLIDAY("공휴일");

    private final String description;
}
