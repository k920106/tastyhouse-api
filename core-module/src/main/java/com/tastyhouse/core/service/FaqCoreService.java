package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.faq.dto.FaqCategoryDto;
import com.tastyhouse.core.entity.faq.dto.FaqItemDto;
import com.tastyhouse.core.repository.faq.FaqRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaqCoreService {

    private final FaqRepository faqRepository;

    @Transactional(readOnly = true)
    public List<FaqCategoryDto> findAllActiveCategories() {
        return faqRepository.findAllActiveCategories();
    }

    @Transactional(readOnly = true)
    public List<FaqItemDto> findFaqItems(Long categoryId) {
        if (categoryId == null) {
            return faqRepository.findAllActiveItems();
        }
        return faqRepository.findActiveItemsByCategoryId(categoryId);
    }
}
