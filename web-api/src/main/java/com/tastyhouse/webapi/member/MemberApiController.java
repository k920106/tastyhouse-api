package com.tastyhouse.webapi.member;

import com.tastyhouse.core.common.CommonResponse;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.coupon.response.MemberCouponListItemResponse;
import com.tastyhouse.webapi.member.request.UpdateProfileRequest;
import com.tastyhouse.webapi.member.response.MemberProfileResponse;
import com.tastyhouse.webapi.member.response.MyBookmarkedPlaceListItemResponse;
import com.tastyhouse.webapi.member.response.MyReviewListItemResponse;
import com.tastyhouse.webapi.member.response.MyReviewStatsResponse;
import com.tastyhouse.webapi.member.response.PointHistoryResponse;
import com.tastyhouse.webapi.member.response.PointResponse;
import com.tastyhouse.webapi.member.response.UsablePointResponse;
import com.tastyhouse.webapi.service.CustomUserDetails;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Tag(name = "Member", description = "회원 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {

    private final MemberService memberService;

    @Operation(summary = "내 프로필 조회", description = "로그인한 회원의 프로필 정보를 조회합니다. (마이페이지용)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = MemberProfileResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    @GetMapping("/v1/me")
    public ResponseEntity<CommonResponse<MemberProfileResponse>> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<MemberProfileResponse> memberProfile = memberService.getMemberProfile(userDetails.getMemberId());
        return memberProfile
            .map(profile -> ResponseEntity.ok(CommonResponse.success(profile)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "프로필 수정", description = "로그인한 회원의 프로필 정보를 수정합니다. (닉네임, 상태메시지, 프로필 이미지)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검증 실패)")
    })
    @PutMapping("/v1/me/profile")
    public ResponseEntity<CommonResponse<Void>> updateMyProfile(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody UpdateProfileRequest request
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        memberService.updateMemberProfile(
            userDetails.getMemberId(),
            request.getNickname(),
            request.getStatusMessage(),
            request.getProfileImageFileId()
        );

        return ResponseEntity.ok(CommonResponse.success(null));
    }

    @Operation(summary = "내 리뷰 통계 조회", description = "로그인한 회원의 리뷰 통계(전체 리뷰 개수)를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = MyReviewStatsResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    @GetMapping("/v1/me/review-stats")
    public ResponseEntity<CommonResponse<MyReviewStatsResponse>> getMyReviewStats(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        MyReviewStatsResponse stats = memberService.getMyReviewStats(memberId);
        return ResponseEntity.ok(CommonResponse.success(stats));
    }

    @Operation(summary = "보유 포인트 조회", description = "현재 로그인한 회원의 사용 가능한 포인트와 이번달 소멸 예정 포인트를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    @GetMapping("/v1/me/point")
    public ResponseEntity<CommonResponse<PointResponse>> getMyPoint(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        PointResponse pointResponse = memberService.getMemberPoint(memberId);
        return ResponseEntity.ok(CommonResponse.success(pointResponse));
    }


    @Operation(summary = "보유 쿠폰 목록 조회", description = "현재 로그인한 회원이 보유한 모든 쿠폰을 조회합니다. (사용 여부 무관)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    @GetMapping("/v1/me/coupons")
    public ResponseEntity<CommonResponse<List<MemberCouponListItemResponse>>> getMyCoupons(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        List<MemberCouponListItemResponse> coupons = memberService.getMemberCoupons(memberId);
        return ResponseEntity.ok(CommonResponse.success(coupons));
    }

    @Operation(summary = "사용 가능한 쿠폰 목록 조회", description = "현재 로그인한 회원이 보유한 사용 가능한 쿠폰을 조회합니다. (미사용 + 유효기간 내)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    @GetMapping("/v1/me/coupons/available")
    public ResponseEntity<CommonResponse<List<MemberCouponListItemResponse>>> getMyAvailableCoupons(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        List<MemberCouponListItemResponse> coupons = memberService.getAvailableMemberCoupons(memberId);
        return ResponseEntity.ok(CommonResponse.success(coupons));
    }

    @Operation(summary = "포인트 내역 조회", description = "사용 가능 포인트, 이번달 소멸 예정 포인트, 포인트 적립/사용 내역 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    @GetMapping("/v1/me/point/history")
    public ResponseEntity<CommonResponse<PointHistoryResponse>> getMyPointHistory(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        PointHistoryResponse pointHistory = memberService.getPointHistory(memberId);
        return ResponseEntity.ok(CommonResponse.success(pointHistory));
    }

    @Operation(summary = "사용 가능 포인트 조회 (주문용)", description = "주문 시 사용 가능한 포인트를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    @GetMapping("/v1/me/point/usable")
    public ResponseEntity<CommonResponse<UsablePointResponse>> getMyUsablePoint(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        UsablePointResponse usablePoint = memberService.getUsablePoint(memberId);
        return ResponseEntity.ok(CommonResponse.success(usablePoint));
    }

    @Operation(summary = "내가 작성한 리뷰 목록 조회", description = "로그인한 회원이 작성한 리뷰 목록을 페이징하여 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    @GetMapping("/v1/me/reviews")
    public ResponseEntity<CommonResponse<List<MyReviewListItemResponse>>> getMyReviews(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size
    ) {
        Long memberId = userDetails.getMemberId();
        PageRequest pageRequest = new PageRequest(page, size);
        PageResult<MyReviewListItemResponse> pageResult = memberService.getMyReviews(memberId, pageRequest);
        CommonResponse<List<MyReviewListItemResponse>> response = CommonResponse.success(
            pageResult.getContent(),
            pageResult.getCurrentPage(),
            pageResult.getPageSize(),
            pageResult.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내가 즐겨찾기한 플레이스 목록 조회", description = "로그인한 회원이 북마크한 플레이스 목록을 페이징하여 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    @GetMapping("/v1/me/bookmarks")
    public ResponseEntity<CommonResponse<List<MyBookmarkedPlaceListItemResponse>>> getMyBookmarkedPlaces(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size
    ) {
        Long memberId = userDetails.getMemberId();
        PageRequest pageRequest = new PageRequest(page, size);
        PageResult<MyBookmarkedPlaceListItemResponse> pageResult = memberService.getMyBookmarkedPlaces(memberId, pageRequest);
        CommonResponse<List<MyBookmarkedPlaceListItemResponse>> response = CommonResponse.success(
            pageResult.getContent(),
            pageResult.getCurrentPage(),
            pageResult.getPageSize(),
            pageResult.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }
}
