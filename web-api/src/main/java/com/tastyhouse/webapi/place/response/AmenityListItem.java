package com.tastyhouse.webapi.place.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AmenityListItem {
    private final String code;
    private final String name;
    private final String imageUrlOn;
    private final String imageUrlOff;
}
