package com.tastyhouse.webapi.faq.response;

public record FaqItem(
        Long id,
        Long categoryId,
        String question,
        String answer,
        Integer sort
) {
}
