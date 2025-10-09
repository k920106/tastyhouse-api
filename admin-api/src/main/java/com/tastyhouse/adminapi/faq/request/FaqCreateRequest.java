package com.tastyhouse.adminapi.faq.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FaqCreateRequest {
    private Long companyId;
    private String title;
    private String content;
    @JsonProperty("active")
    private boolean active;
    private Integer sort;
}
