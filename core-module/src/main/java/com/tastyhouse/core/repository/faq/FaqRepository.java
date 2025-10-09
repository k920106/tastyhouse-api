package com.tastyhouse.core.repository.faq;

import com.tastyhouse.core.entity.faq.dto.FaqListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FaqRepository {

    Page<FaqListItemDto> findAllWithFilter(Long companyId, String title, Boolean active, Pageable pageable);
}
