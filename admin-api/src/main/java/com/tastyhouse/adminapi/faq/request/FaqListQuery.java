package com.tastyhouse.adminapi.faq.request;

import lombok.Data;

@Data
public class FaqListQuery {
    private int page = 0;
    private int size = 10;
    private Long companyId;
    private String title;
    private Boolean active;
}
