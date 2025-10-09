package com.tastyhouse.webapi.places.request;

public record PlaceNearRequest(
    Double latitude,
    Double longitude
) {
}
