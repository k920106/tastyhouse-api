package com.tastyhouse.core.entity.faq.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class FaqCategoryDto {

    private final Long id;
    private final String name;
    private final Integer sort;

    @QueryProjection
    public FaqCategoryDto(Long id, String name, Integer sort) {
        this.id = id;
        this.name = name;
        this.sort = sort;
    }
}
