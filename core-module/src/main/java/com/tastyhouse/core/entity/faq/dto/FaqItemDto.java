package com.tastyhouse.core.entity.faq.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class FaqItemDto {

    private final Long id;
    private final Long faqCategoryId;
    private final String question;
    private final String answer;
    private final Integer sort;

    @QueryProjection
    public FaqItemDto(Long id, Long faqCategoryId, String question, String answer, Integer sort) {
        this.id = id;
        this.faqCategoryId = faqCategoryId;
        this.question = question;
        this.answer = answer;
        this.sort = sort;
    }
}
