package com.tastyhouse.webapi.banner.response;

public record BannerListItem(
        Long id,
        String title,
        String imageUrl,
        String linkUrl
) {
}
