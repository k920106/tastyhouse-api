package com.tastyhouse.webapi.review;

import com.tastyhouse.core.common.PagedApiResponse;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.review.request.ReviewCreateRequest;
import com.tastyhouse.webapi.review.response.BestReviewListItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review", description = "리뷰 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewApiController {

    private final ReviewService reviewService;

    @PostMapping
//    public void create(@RequestBody ReviewCreateRequest reviewCreateRequest) {
    public void create(@ModelAttribute ReviewCreateRequest reviewCreateRequest) {
        System.out.println("hi");
    }

    @Operation(
        summary = "베스트 리뷰 목록 조회",
        description = "평점이 높은 순으로 정렬된 베스트 리뷰 목록을 페이징하여 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PagedApiResponse.class))
        )
    })
    @GetMapping("/v1/best")
    public ResponseEntity<PagedApiResponse<BestReviewListItem>> getBestReviewList(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
    ) {
        PageRequest pageRequest = new PageRequest(page, size);

        PageResult<BestReviewListItem> pageResult = reviewService.findBestReviewList(
            pageRequest
        );

        PagedApiResponse<BestReviewListItem> response = PagedApiResponse.success(
            pageResult.getContent(),
            page,
            size,
            pageResult.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }
}
