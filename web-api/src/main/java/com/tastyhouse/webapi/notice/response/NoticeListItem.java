package com.tastyhouse.webapi.notice.response;

import java.time.LocalDateTime;

public record NoticeListItem(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt
) {
}
