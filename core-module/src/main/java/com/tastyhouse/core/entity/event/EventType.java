package com.tastyhouse.core.entity.event;

import lombok.Getter;

@Getter
public enum EventType {
    RANKING("랭킹 이벤트"),
    PRODUCT_DISCOUNT("상품 할인 이벤트"),
    ;

    private final String description;

    EventType(String description) {
        this.description = description;
    }
}
