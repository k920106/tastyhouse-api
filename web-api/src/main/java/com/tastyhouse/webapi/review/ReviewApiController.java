package com.tastyhouse.webapi.review;

import com.tastyhouse.core.common.CommonResponse;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.review.request.CommentCreateRequest;
import com.tastyhouse.webapi.review.request.ReplyCreateRequest;
import com.tastyhouse.webapi.review.request.ReviewCreateRequest;
import com.tastyhouse.webapi.review.request.ReviewType;
import com.tastyhouse.webapi.review.response.*;
import com.tastyhouse.webapi.service.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(summary = "베스트 리뷰 목록 조회", description = "평점이 높은 순으로 정렬된 베스트 리뷰 목록을 페이징하여 조회합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/best")
    public ResponseEntity<CommonResponse<List<BestReviewListItem>>> getBestReviewList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        PageResult<BestReviewListItem> pageResult = reviewService.findBestReviewList(pageRequest);
        CommonResponse<List<BestReviewListItem>> response = CommonResponse.success(pageResult.getContent(), page, size, pageResult.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "최신 리뷰 목록 조회", description = "최신 리뷰 목록을 페이징하여 조회합니다. type이 ALL이면 전체, FOLLOWING이면 팔로잉한 사용자의 리뷰만 조회합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/latest")
    public ResponseEntity<CommonResponse<List<LatestReviewListItem>>> getLatestReviewList(@Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page, @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size, @Parameter(description = "조회 타입 (ALL: 전체, FOLLOWING: 팔로잉)", example = "ALL") @RequestParam(defaultValue = "ALL") ReviewType type, @AuthenticationPrincipal CustomUserDetails userDetails) {
        PageRequest pageRequest = new PageRequest(page, size);
        Long memberId = userDetails != null ? userDetails.getMemberId() : null;
        PageResult<LatestReviewListItem> pageResult = reviewService.findLatestReviewList(pageRequest, type, memberId);
        CommonResponse<List<LatestReviewListItem>> response = CommonResponse.success(pageResult.getContent(), page, size, pageResult.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "리뷰 상세 조회", description = "리뷰 ID로 리뷰 상세 정보를 조회합니다. 리뷰 태그 정보도 함께 조회됩니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ReviewDetailResponse.class))), @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")})
    @GetMapping("/v1/{reviewId}")
    public ResponseEntity<CommonResponse<ReviewDetailResponse>> getReviewDetail(@Parameter(description = "리뷰 ID", example = "1") @PathVariable Long reviewId) {
        return reviewService.findReviewDetail(reviewId)
                .map(detail -> ResponseEntity.ok(CommonResponse.success(detail)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "리뷰 상세 정보 조회 (상품 정보 포함)", description = "리뷰 ID로 리뷰 상세 정보와 연결된 상품 정보를 함께 조회합니다. 평점, 유저 정보, 작성일, 내용, 이미지, 태그 정보가 포함됩니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ReviewProductResponse.class))), @ApiResponse(responseCode = "404", description = "리뷰 또는 상품을 찾을 수 없음")})
    @GetMapping("/v1/{reviewId}/product")
    public ResponseEntity<CommonResponse<ReviewProductResponse>> getReviewProduct(@Parameter(description = "리뷰 ID", example = "1") @PathVariable Long reviewId) {
        return reviewService.findReviewProduct(reviewId)
                .map(product -> ResponseEntity.ok(CommonResponse.success(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "리뷰 좋아요 여부 조회", description = "리뷰가 현재 사용자에 의해 좋아요되었는지 여부를 조회합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공")})
    @GetMapping("/v1/{reviewId}/like")
    public ResponseEntity<CommonResponse<ReviewLikeStatusResponse>> isLiked(@PathVariable Long reviewId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ReviewLikeStatusResponse liked;
        if (userDetails == null) {
            liked = new ReviewLikeStatusResponse(false);
        } else {
            Long memberId = userDetails.getMemberId();
            liked = reviewService.isLiked(reviewId, memberId);
        }
        return ResponseEntity.ok(CommonResponse.success(liked));
    }

    @Operation(summary = "리뷰 좋아요 토글", description = "리뷰에 좋아요를 토글합니다. 이미 좋아요한 경우 취소되고, 아닌 경우 좋아요가 추가됩니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "좋아요 토글 성공", content = @Content(schema = @Schema(implementation = ReviewLikeResponse.class))), @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")})
    @PostMapping("/v1/{reviewId}/like")
    public ResponseEntity<CommonResponse<ReviewLikeResponse>> toggleReviewLike(@Parameter(description = "리뷰 ID", example = "1") @PathVariable Long reviewId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        boolean liked = reviewService.toggleReviewLike(reviewId, userDetails.getMemberId());
        return ResponseEntity.ok(CommonResponse.success(new ReviewLikeResponse(liked)));
    }

    @Operation(summary = "댓글 등록", description = "리뷰에 댓글을 등록합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "댓글 등록 성공", content = @Content(schema = @Schema(implementation = CommentResponse.class))), @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")})
    @PostMapping("/v1/{reviewId}/comments")
    public ResponseEntity<CommonResponse<CommentResponse>> createComment(@Parameter(description = "리뷰 ID", example = "1") @PathVariable Long reviewId, @Valid @RequestBody CommentCreateRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        CommentResponse response = reviewService.createComment(reviewId, userDetails.getMemberId(), request.getContent());
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "답글 등록", description = "댓글에 답글을 등록합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "답글 등록 성공", content = @Content(schema = @Schema(implementation = ReplyResponse.class))), @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")})
    @PostMapping("/v1/comments/{commentId}/replies")
    public ResponseEntity<CommonResponse<ReplyResponse>> createReply(@Parameter(description = "댓글 ID", example = "1") @PathVariable Long commentId, @Valid @RequestBody ReplyCreateRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        ReplyResponse response = reviewService.createReply(commentId, userDetails.getMemberId(), request.getReplyToMemberId(), request.getContent());
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "댓글 및 답글 조회", description = "리뷰의 모든 댓글과 답글을 조회합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommentListResponse.class)))})
    @GetMapping("/v1/{reviewId}/comments")
    public ResponseEntity<CommonResponse<CommentListResponse>> getComments(@Parameter(description = "리뷰 ID", example = "1") @PathVariable Long reviewId) {
        CommentListResponse response = reviewService.findCommentsWithReplies(reviewId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
