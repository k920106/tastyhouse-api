package com.tastyhouse.webapi.banner.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BannerListItem {
    private final Long id;
    private final String title;
    private final String imageUrl;
    private final String linkUrl;
}
