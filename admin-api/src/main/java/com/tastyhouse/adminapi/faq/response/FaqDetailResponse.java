package com.tastyhouse.adminapi.faq.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FaqDetailResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private String title;
    private String content;
    private boolean active;
    private Integer sort;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
