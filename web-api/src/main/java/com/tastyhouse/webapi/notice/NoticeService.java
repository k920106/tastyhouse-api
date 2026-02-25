package com.tastyhouse.webapi.notice;

import com.tastyhouse.core.common.PageResult;
import com.tastyhouse.core.entity.notice.dto.NoticeListItemDto;
import com.tastyhouse.core.service.NoticeCoreService;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.notice.response.NoticeListItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeCoreService noticeCoreService;

    public PageResult<NoticeListItem> findNoticeList(PageRequest pageRequest) {
        return noticeCoreService.findAllWithPagination(
            pageRequest.getPage(), pageRequest.getSize()
        ).map(this::convertToNoticeListItem);
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
