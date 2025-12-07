package com.tastyhouse.webapi.place.request;

public record PlaceNearRequest(
    Double latitude,
    Double longitude
) {
}
