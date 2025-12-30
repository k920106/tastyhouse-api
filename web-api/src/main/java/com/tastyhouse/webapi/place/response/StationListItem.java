package com.tastyhouse.webapi.place.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StationListItem {
    private final Long id;
    private final String name;
}
