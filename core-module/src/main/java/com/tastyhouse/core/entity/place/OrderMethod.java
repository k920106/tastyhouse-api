package com.tastyhouse.core.entity.place;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderMethod {

    TABLE_ORDER("테이블 오더"),
    RESERVATION("예약"),
    DELIVERY("배달"),
    TAKEOUT("포장");

    private final String displayName;
}
