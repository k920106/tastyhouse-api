package com.tastyhouse.webapi.prize;

import com.tastyhouse.core.common.ApiResponse;
import com.tastyhouse.webapi.prize.response.PrizeItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Prize", description = "경품 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/prizes")
public class PrizeApiController {

    private final PrizeService prizeService;

    @Operation(
        summary = "현재 진행중인 경품 목록 조회",
        description = "현재 진행중인 프로모션의 등수별 경품 목록을 조회합니다. (1등, 2등, 3등 등)"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/v1")
    public ResponseEntity<ApiResponse<List<PrizeItem>>> getActivePrizeList() {
        List<PrizeItem> prizes = prizeService.getActivePrizes();

        ApiResponse<List<PrizeItem>> response = ApiResponse.success(prizes);

        return ResponseEntity.ok(response);
    }
}