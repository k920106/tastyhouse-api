package com.tastyhouse.adminapi.notice;

import com.tastyhouse.adminapi.common.PageRequest;
import com.tastyhouse.adminapi.common.PageResult;
import com.tastyhouse.adminapi.notice.request.NoticeCreateRequest;
import com.tastyhouse.adminapi.notice.request.NoticeListQuery;
import com.tastyhouse.adminapi.notice.request.NoticeUpdateRequest;
import com.tastyhouse.adminapi.notice.response.NoticeCreateResponse;
import com.tastyhouse.adminapi.notice.response.NoticeDetailResponse;
import com.tastyhouse.adminapi.notice.response.NoticeListItem;
import com.tastyhouse.core.common.ApiResponse;
import com.tastyhouse.core.common.PagedApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeApiController {

    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<PagedApiResponse<NoticeListItem>> getNoticeList(@ModelAttribute NoticeListQuery query) {
        PageRequest pageRequest = new PageRequest(query.getPage(), query.getSize());

        PageResult<NoticeListItem> pageResult = noticeService.findNoticeList(
            query.getCompanyId(),
            query.getTitle(),
            query.getActive(),
            query.getStartDate(),
            query.getEndDate(),
            pageRequest
        );

        PagedApiResponse<NoticeListItem> response = PagedApiResponse.success(
            pageResult.getContent(),
            query.getPage(),
            query.getSize(),
            pageResult.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NoticeCreateResponse>> createNotice(@RequestBody NoticeCreateRequest request) {
        Long noticeId = noticeService.createNotice(request);
        NoticeCreateResponse data = new NoticeCreateResponse(noticeId);
        ApiResponse<NoticeCreateResponse> response = ApiResponse.success(data, "OK");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NoticeDetailResponse>> getNoticeDetail(@PathVariable Long id) {
        NoticeDetailResponse data = noticeService.findNoticeDetail(id);
        ApiResponse<NoticeDetailResponse> response = ApiResponse.success(data);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateNotice(
        @PathVariable Long id,
        @RequestBody NoticeUpdateRequest request
    ) {
        noticeService.updateNotice(id, request);
        ApiResponse<Void> response = ApiResponse.success(null, "OK");
        return ResponseEntity.ok(response);
    }
}
