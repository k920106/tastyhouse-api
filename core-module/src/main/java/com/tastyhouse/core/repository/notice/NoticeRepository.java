package com.tastyhouse.core.repository.notice;

import com.tastyhouse.core.entity.notice.dto.NoticeListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepository {

    Page<NoticeListItemDto> findAllWithFilter(Pageable pageable);
}
