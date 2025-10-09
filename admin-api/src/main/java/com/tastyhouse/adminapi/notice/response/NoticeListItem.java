package com.tastyhouse.adminapi.notice.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeListItem {
    private final Long id;
    private final String title;
    private final LocalDateTime createdAt;
    private final Boolean active;
}
