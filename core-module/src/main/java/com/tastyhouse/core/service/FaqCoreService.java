package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.faq.dto.FaqCategoryDto;
import com.tastyhouse.core.entity.faq.dto.FaqItemDto;
import com.tastyhouse.core.repository.faq.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaqCoreService {

    private final FaqRepository faqRepository;

    public List<FaqCategoryDto> findAllActiveCategories() {
        return faqRepository.findAllActiveCategories();
    }

    public List<FaqItemDto> findFaqItems(Long categoryId) {
        if (categoryId == null) {
            return faqRepository.findAllActiveItems();
        }
        return faqRepository.findActiveItemsByCategoryId(categoryId);
    }
}
