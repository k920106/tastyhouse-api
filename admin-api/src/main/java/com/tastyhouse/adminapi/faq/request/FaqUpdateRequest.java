package com.tastyhouse.adminapi.faq.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FaqUpdateRequest {
    private Long companyId;
    private String title;
    private String content;
    @JsonProperty("active")
    private Boolean active;
    private Integer sort;
}
