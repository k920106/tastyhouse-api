package com.tastyhouse.adminapi.notice.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeDetailResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private String title;
    private String content;
    private boolean active;
    private boolean top;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
