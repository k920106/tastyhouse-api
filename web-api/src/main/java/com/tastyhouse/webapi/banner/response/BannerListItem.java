package com.tastyhouse.webapi.banner.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BannerListItem {
    private final Long id;
    private final String title;
    private final String imageUrl;
    private final String linkUrl;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Integer sort;
    private final Boolean active;
}