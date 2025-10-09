package com.tastyhouse.adminapi.notice.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class NoticeListQuery {
    private int page = 0;
    private int size = 10;
    private Long companyId;
    private String title;
    private Boolean active;
    private LocalDate startDate;
    private LocalDate endDate;
}
