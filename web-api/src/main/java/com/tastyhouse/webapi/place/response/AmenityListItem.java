package com.tastyhouse.webapi.place.response;

public record AmenityListItem(
        String code,
        String name,
        String imageUrlOn,
        String imageUrlOff
) {
}
