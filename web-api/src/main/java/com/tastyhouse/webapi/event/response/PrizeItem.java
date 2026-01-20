package com.tastyhouse.webapi.event.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrizeItem {

    private Long id;
    private Integer prizeRank;
    private String name;
    private String brand;
    private String imageUrl;
}
