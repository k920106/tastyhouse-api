package com.tastyhouse.webapi.faq;

import com.tastyhouse.core.entity.faq.dto.FaqCategoryDto;
import com.tastyhouse.core.entity.faq.dto.FaqItemDto;
import com.tastyhouse.core.service.FaqCoreService;
import com.tastyhouse.webapi.faq.response.FaqCategoryItem;
import com.tastyhouse.webapi.faq.response.FaqItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqCoreService faqCoreService;

    public List<FaqCategoryItem> findCategories() {
        return faqCoreService.findAllActiveCategories().stream()
                .map(this::toFaqCategoryItem)
                .toList();
    }

    public List<FaqItem> findFaqItems(Long categoryId) {
        return faqCoreService.findFaqItems(categoryId).stream()
                .map(this::toFaqItem)
                .toList();
    }

    private FaqCategoryItem toFaqCategoryItem(FaqCategoryDto dto) {
        return FaqCategoryItem.builder()
                .id(dto.getId())
                .name(dto.getName())
                .sort(dto.getSort())
                .build();
    }

    private FaqItem toFaqItem(FaqItemDto dto) {
        return FaqItem.builder()
                .id(dto.getId())
                .categoryId(dto.getFaqCategoryId())
                .question(dto.getQuestion())
                .answer(dto.getAnswer())
                .sort(dto.getSort())
                .build();
    }
}
