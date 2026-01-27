package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.notice.Notice;
import com.tastyhouse.core.entity.notice.dto.NoticeListItemDto;
import com.tastyhouse.core.repository.notice.NoticeJpaRepository;
import com.tastyhouse.core.repository.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeCoreService {

    private final NoticeRepository noticeRepository;
    private final NoticeJpaRepository noticeJpaRepository;

    public NoticePageResult findAllWithPagination(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<NoticeListItemDto> noticePage = noticeRepository.findAllWithFilter(pageRequest);

        return new NoticePageResult(
            noticePage.getContent(),
            noticePage.getTotalElements(),
            noticePage.getTotalPages(),
            noticePage.getNumber(),
            noticePage.getSize()
        );
    }

    public Notice findById(Long id) {
        return noticeJpaRepository.findById(id).orElse(null);
    }

    public static class NoticePageResult {
        private final List<NoticeListItemDto> content;
        private final long totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;

        public NoticePageResult(List<NoticeListItemDto> content, long totalElements, int totalPages, int currentPage, int pageSize) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }

        public List<NoticeListItemDto> getContent() { return content; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
    }
}
