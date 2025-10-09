package com.tastyhouse.adminapi.notice.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NoticeUpdateRequest {
    private Long companyId;
    private String title;
    private String content;
    @JsonProperty("active")
    private Boolean active;
    @JsonProperty("top")
    private Boolean top;
}
