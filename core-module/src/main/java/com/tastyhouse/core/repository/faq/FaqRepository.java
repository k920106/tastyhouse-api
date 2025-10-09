package com.tastyhouse.core.repository.faq;

import com.tastyhouse.core.entity.faq.dto.FaqListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface FaqRepository {

    Page<FaqListItemDto> findAllWithFilter(Long companyId, String title, Boolean active,
                                           LocalDate startDate, LocalDate endDate, Pageable pageable);
}
