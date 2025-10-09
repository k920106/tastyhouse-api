package com.tastyhouse.core.repository.notice;

import com.tastyhouse.core.entity.notice.dto.NoticeListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface NoticeRepository {

    Page<NoticeListItemDto> findAllWithFilter(Long companyId, String title, Boolean active, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
