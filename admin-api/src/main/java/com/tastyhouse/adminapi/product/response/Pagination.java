package com.tastyhouse.adminapi.product.response;

public record Pagination(
    int draw,
    long total,
    long filtered
) {
}
