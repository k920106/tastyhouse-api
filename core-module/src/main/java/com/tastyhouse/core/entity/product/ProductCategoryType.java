package com.tastyhouse.core.entity.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductCategoryType {

    SIGNATURE("시그니처 메뉴"),
    MAIN("메인 메뉴"),
    SIDE("사이드 메뉴"),
    DRINK("음료"),
    DESSERT("디저트"),
    SET("세트 메뉴"),
    SEASONAL("계절 메뉴"),
    ETC("기타");

    private final String displayName;
}
