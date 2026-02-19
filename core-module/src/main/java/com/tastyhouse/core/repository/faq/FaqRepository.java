package com.tastyhouse.core.repository.faq;

import com.tastyhouse.core.entity.faq.dto.FaqCategoryDto;
import com.tastyhouse.core.entity.faq.dto.FaqItemDto;

import java.util.List;

public interface FaqRepository {

    List<FaqCategoryDto> findAllActiveCategories();

    List<FaqItemDto> findAllActiveItems();

    List<FaqItemDto> findActiveItemsByCategoryId(Long categoryId);
}
