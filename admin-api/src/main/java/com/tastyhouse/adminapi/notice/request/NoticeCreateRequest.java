package com.tastyhouse.adminapi.notice.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NoticeCreateRequest {
    private Long companyId;
    private String title;
    private String content;
    @JsonProperty("active")
    private boolean active;
    @JsonProperty("top")
    private boolean top;
}
