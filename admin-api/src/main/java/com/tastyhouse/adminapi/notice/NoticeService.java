package com.tastyhouse.adminapi.notice;

import com.tastyhouse.adminapi.common.PageRequest;
import com.tastyhouse.adminapi.common.PageResult;
import com.tastyhouse.adminapi.exception.ResourceNotFoundException;
import com.tastyhouse.adminapi.notice.request.NoticeCreateRequest;
import com.tastyhouse.adminapi.notice.response.NoticeDetailResponse;
import com.tastyhouse.adminapi.notice.response.NoticeListItem;
import com.tastyhouse.core.entity.company.Company;
import com.tastyhouse.core.entity.notice.Notice;
import com.tastyhouse.core.entity.notice.dto.NoticeListItemDto;
import com.tastyhouse.core.service.CompanyCoreService;
import com.tastyhouse.core.service.NoticeCoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeCoreService noticeCoreService;
    private final CompanyCoreService companyCoreService;

    public PageResult<NoticeListItem> findNoticeList(Long companyId, String title, Boolean active, LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {
        NoticeCoreService.NoticePageResult coreResult = noticeCoreService.findAllWithPagination(
            companyId, title, active, startDate, endDate,
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

    public Long createNotice(NoticeCreateRequest request) {
        Notice notice = noticeCoreService.createNotice(
            request.getCompanyId(),
            request.getTitle(),
            request.getContent(),
            request.isActive(),
            request.isTop()
        );
        return notice.getId();
    }

    public NoticeDetailResponse findNoticeDetail(Long id) {
        Notice notice = noticeCoreService.findById(id);
        if (notice == null) {
            throw new ResourceNotFoundException("공지사항을 찾을 수 없습니다. ID: " + id);
        }

        Company company = companyCoreService.findById(notice.getCompanyId());

        return NoticeDetailResponse.builder()
            .id(notice.getId())
            .companyId(notice.getCompanyId())
            .companyName(company.getName())
            .title(notice.getTitle())
            .content(notice.getContent())
            .active(notice.getActive())
            .top(notice.getTop())
            .createdAt(notice.getCreatedAt())
            .updatedAt(notice.getUpdatedAt())
            .build();
    }

    public Long updateNotice(Long id, com.tastyhouse.adminapi.notice.request.NoticeUpdateRequest request) {
        Notice notice = noticeCoreService.updateNotice(
            id,
            request.getCompanyId(),
            request.getTitle(),
            request.getContent(),
            request.getActive(),
            request.getTop()
        );

        if (notice == null) {
            throw new ResourceNotFoundException("공지사항을 찾을 수 없습니다. ID: " + id);
        }

        return notice.getId();
    }

    private NoticeListItem convertToNoticeListItem(NoticeListItemDto dto) {
        return NoticeListItem.builder()
            .id(dto.getId())
            .title(dto.getTitle())
            .active(dto.isActive())
            .createdAt(dto.getCreatedAt())
            .build();
    }
}
