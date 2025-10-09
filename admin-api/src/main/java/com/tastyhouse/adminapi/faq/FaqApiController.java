package com.tastyhouse.adminapi.faq;

import com.tastyhouse.adminapi.common.PageRequest;
import com.tastyhouse.adminapi.common.PageResult;
import com.tastyhouse.adminapi.faq.request.FaqCreateRequest;
import com.tastyhouse.adminapi.faq.request.FaqListQuery;
import com.tastyhouse.adminapi.faq.request.FaqUpdateRequest;
import com.tastyhouse.adminapi.faq.response.FaqCreateResponse;
import com.tastyhouse.adminapi.faq.response.FaqDetailResponse;
import com.tastyhouse.adminapi.faq.response.FaqListItem;
import com.tastyhouse.core.common.ApiResponse;
import com.tastyhouse.core.common.PagedApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/faqs")
public class FaqApiController {

    private final FaqService faqService;

    @GetMapping
    public ResponseEntity<PagedApiResponse<FaqListItem>> getFaqList(@ModelAttribute FaqListQuery query) {
        PageRequest pageRequest = new PageRequest(query.getPage(), query.getSize());

        PageResult<FaqListItem> pageResult = faqService.findFaqList(
            query.getCompanyId(),
            query.getTitle(),
            query.getActive(),
            pageRequest
        );

        PagedApiResponse<FaqListItem> response = PagedApiResponse.success(
            pageResult.getContent(),
            query.getPage(),
            query.getSize(),
            pageResult.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FaqCreateResponse>> createFaq(@RequestBody FaqCreateRequest request) {
        Long faqId = faqService.createFaq(request);
        FaqCreateResponse data = new FaqCreateResponse(faqId);
        ApiResponse<FaqCreateResponse> response = ApiResponse.success(data, "OK");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FaqDetailResponse>> getFaqDetail(@PathVariable Long id) {
        FaqDetailResponse data = faqService.findFaqDetail(id);
        ApiResponse<FaqDetailResponse> response = ApiResponse.success(data);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateFaq(
        @PathVariable Long id,
        @RequestBody FaqUpdateRequest request
    ) {
        faqService.updateFaq(id, request);
        ApiResponse<Void> response = ApiResponse.success(null, "OK");
        return ResponseEntity.ok(response);
    }
}
