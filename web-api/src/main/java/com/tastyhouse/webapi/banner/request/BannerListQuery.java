package com.tastyhouse.webapi.banner.request;

import lombok.Data;

@Data
public class BannerListQuery {
    private String title;
    private Boolean active;
}
