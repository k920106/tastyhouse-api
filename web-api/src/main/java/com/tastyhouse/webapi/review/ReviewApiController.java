package com.tastyhouse.webapi.review;

import com.tastyhouse.core.common.ApiResponse;
import com.tastyhouse.core.common.PagedApiResponse;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.review.request.ReviewCreateRequest;
import com.tastyhouse.webapi.review.request.ReviewType;
import com.tastyhouse.webapi.review.response.BestReviewListItem;
import com.tastyhouse.webapi.review.response.LatestReviewListItem;
import com.tastyhouse.webapi.review.response.ReviewDetailResponse;
import com.tastyhouse.webapi.service.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
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

    @Operation(
        summary = "최신 리뷰 목록 조회",
        description = "최신 리뷰 목록을 페이징하여 조회합니다. type이 ALL이면 전체, FOLLOWING이면 팔로잉한 사용자의 리뷰만 조회합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PagedApiResponse.class))
        )
    })
    @GetMapping("/v1/latest")
    public ResponseEntity<PagedApiResponse<LatestReviewListItem>> getLatestReviewList(
        @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "페이지 크기", example = "10")
        @RequestParam(defaultValue = "10") int size,
        @Parameter(description = "조회 타입 (ALL: 전체, FOLLOWING: 팔로잉)", example = "ALL")
        @RequestParam(defaultValue = "ALL") ReviewType type,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PageRequest pageRequest = new PageRequest(page, size);
        Long memberId = userDetails != null ? userDetails.getMemberId() : null;

        PageResult<LatestReviewListItem> pageResult = reviewService.findLatestReviewList(
            pageRequest,
            type,
            memberId
        );

        PagedApiResponse<LatestReviewListItem> response = PagedApiResponse.success(
            pageResult.getContent(),
            page,
            size,
            pageResult.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "리뷰 상세 조회",
        description = "리뷰 ID로 리뷰 상세 정보를 조회합니다. 리뷰 태그 정보도 함께 조회됩니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ReviewDetailResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "리뷰를 찾을 수 없음"
        )
    })
    @GetMapping("/v1/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDetailResponse>> getReviewDetail(
        @Parameter(description = "리뷰 ID", example = "1")
        @PathVariable Long reviewId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails != null ? userDetails.getMemberId() : null;
        return reviewService.findReviewDetail(reviewId, memberId)
            .map(detail -> ResponseEntity.ok(ApiResponse.success(detail)))
            .orElse(ResponseEntity.notFound().build());
    }
}
