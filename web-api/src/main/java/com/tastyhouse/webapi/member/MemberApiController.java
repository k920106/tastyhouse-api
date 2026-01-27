package com.tastyhouse.webapi.member;

import com.tastyhouse.core.common.CommonResponse;
import com.tastyhouse.webapi.member.response.MemberInfoResponse;
import com.tastyhouse.webapi.member.response.PointResponse;
import com.tastyhouse.webapi.service.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Tag(name = "Member", description = "회원 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {

    private final MemberService memberService;

    @Operation(summary = "내 정보 조회", description = "로그인한 회원의 정보를 조회합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = MemberInfoResponse.class))), @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"), @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")})
    @GetMapping("/v1/me")
    public ResponseEntity<CommonResponse<MemberInfoResponse>> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<MemberInfoResponse> memberInfo = memberService.findMemberInfo(userDetails.getMemberId());
        return memberInfo.map(memberInfoResponse -> ResponseEntity.ok(CommonResponse.success(memberInfoResponse))).orElseGet(() -> ResponseEntity.notFound().build());
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
}
