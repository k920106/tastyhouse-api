package com.tastyhouse.webapi.faq.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FaqItem {

    private final Long id;
    private final Long categoryId;
    private final String question;
    private final String answer;
    private final Integer sort;
}
