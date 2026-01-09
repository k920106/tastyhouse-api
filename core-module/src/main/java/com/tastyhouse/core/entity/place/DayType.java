package com.tastyhouse.core.entity.place;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DayType {

    DAILY("매일"),
    WEEKDAY("평일"),
    WEEKEND("주말"),
    HOLIDAY("공휴일"),
    MONDAY("월요일"),
    TUESDAY("화요일"),
    WEDNESDAY("수요일"),
    THURSDAY("목요일"),
    FRIDAY("금요일"),
    SATURDAY("토요일"),
    SUNDAY("일요일");

    private final String description;
}

/**
 * 매일 12:00 ~23:30
 */

/**
 * 평일 12:00 ~23:30
 * 주말 12:00 ~23:30
 */

/**
 * 월 11:00 - 23:00
 * 화 11:00 - 23:00
 * 수 11:00 - 23:00
 * 목 11:00 - 23:00
 * 금 11:00 - 23:00
 * 토 11:00 - 23:00
 * 일 11:00 - 23:00
 */

/**
 * 월 정기휴무
 * 화 11:00 - 23:00
 * 수 11:00 - 23:00
 * 목 11:00 - 23:00
 * 금 11:00 - 23:00
 * 토 11:00 - 23:00
 * 일 11:00 - 23:00
 */
