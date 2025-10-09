package com.tastyhouse.adminapi.faq.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FaqListItem {
    private final Long id;
    private final String title;
    private final Boolean active;
    private final Integer sort;
}
