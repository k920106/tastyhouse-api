package com.tastyhouse.webapi.faq.response;

public record FaqCategoryItem(
        Long id,
        String name,
        Integer sort
) {
}
