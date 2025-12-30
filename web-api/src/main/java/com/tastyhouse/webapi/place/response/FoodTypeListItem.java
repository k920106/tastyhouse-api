package com.tastyhouse.webapi.place.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FoodTypeListItem {
    private final String code;
    private final String name;
    private final String imageUrl;
}
