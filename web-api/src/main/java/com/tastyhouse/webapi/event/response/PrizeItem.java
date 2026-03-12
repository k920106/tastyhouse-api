package com.tastyhouse.webapi.event.response;

public record PrizeItem(
        Long id,
        Integer prizeRank,
        String name,
        String brand,
        String imageUrl
) {
}
