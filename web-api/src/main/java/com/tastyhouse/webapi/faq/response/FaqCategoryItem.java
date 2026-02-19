package com.tastyhouse.webapi.faq.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FaqCategoryItem {

    private final Long id;
    private final String name;
    private final Integer sort;
}
