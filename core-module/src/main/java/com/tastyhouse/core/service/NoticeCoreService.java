package com.tastyhouse.core.service;

import com.tastyhouse.core.common.PageResult;
import com.tastyhouse.core.entity.notice.Notice;
import com.tastyhouse.core.entity.notice.dto.NoticeListItemDto;
import com.tastyhouse.core.exception.EntityNotFoundException;
import com.tastyhouse.core.exception.ErrorCode;
import com.tastyhouse.core.repository.notice.NoticeJpaRepository;
import com.tastyhouse.core.repository.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeCoreService {

    private final NoticeRepository noticeRepository;
    private final NoticeJpaRepository noticeJpaRepository;

    @Transactional(readOnly = true)
    public PageResult<NoticeListItemDto> findAllWithPagination(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<NoticeListItemDto> noticePage = noticeRepository.findAllWithFilter(pageRequest);
        return PageResult.from(noticePage);
    }

    @Transactional(readOnly = true)
    public Notice findById(Long id) {
        return noticeJpaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOTICE_NOT_FOUND));
    }
}
