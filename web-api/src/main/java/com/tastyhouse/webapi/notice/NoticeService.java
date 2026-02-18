package com.tastyhouse.webapi.notice;

import com.tastyhouse.core.entity.notice.dto.NoticeListItemDto;
import com.tastyhouse.core.service.NoticeCoreService;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.notice.response.NoticeListItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeCoreService noticeCoreService;

    public PageResult<NoticeListItem> findNoticeList(PageRequest pageRequest) {
        NoticeCoreService.NoticePageResult coreResult = noticeCoreService.findAllWithPagination(
            pageRequest.getPage(), pageRequest.getSize()
        );

        List<NoticeListItem> noticeListItems = coreResult.getContent().stream()
            .map(this::convertToNoticeListItem)
            .toList();

        return new PageResult<>(
            noticeListItems,
            coreResult.getTotalElements(),
            coreResult.getTotalPages(),
            coreResult.getCurrentPage(),
            coreResult.getPageSize()
        );
    }

    private NoticeListItem convertToNoticeListItem(NoticeListItemDto dto) {
        return NoticeListItem.builder()
            .id(dto.getId())
            .title(dto.getTitle())
            .content(dto.getContent())
            .createdAt(dto.getCreatedAt())
            .build();
    }
}
