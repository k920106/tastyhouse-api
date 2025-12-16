package com.tastyhouse.webapi.rank;

import com.tastyhouse.core.common.ApiResponse;
import com.tastyhouse.webapi.rank.response.MemberRankItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Rank", description = "랭킹 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ranks")
public class RankApiController {

    private final RankService rankService;

    @Operation(
        summary = "멤버 리뷰 랭킹 조회",
        description = "유저별 리뷰 작성 개수 기준 랭킹을 조회합니다. (전체/월간/주간)"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/v1/members")
    public ResponseEntity<ApiResponse<List<MemberRankItem>>> getMemberRankList(
        @Parameter(description = "랭킹 타입 (ALL, MONTHLY, WEEKLY)", example = "MONTHLY")
        @RequestParam(defaultValue = "ALL") String type,
        @Parameter(description = "조회할 랭킹 개수", example = "100")
        @RequestParam(defaultValue = "100") int limit
    ) {
        List<MemberRankItem> ranks = rankService.getMemberRankList(type, limit);

        ApiResponse<List<MemberRankItem>> response = ApiResponse.success(ranks);

        return ResponseEntity.ok(response);
    }
}