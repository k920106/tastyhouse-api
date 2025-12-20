package com.tastyhouse.webapi.event;

import com.tastyhouse.core.common.ApiResponse;
import com.tastyhouse.webapi.event.response.EventDurationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Event", description = "이벤트 관리 API")
@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventApiController {

    private final EventService eventService;

    @Operation(
        summary = "진행중인 랭킹 이벤트 기간 조회",
        description = "현재 진행중인 랭킹 이벤트의 시작일자와 종료일자를 조회합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "진행중인 랭킹 이벤트 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/v1/ranking/duration")
    public ResponseEntity<ApiResponse<EventDurationResponse>> getRankingEventDuration() {
        return eventService.getRankingEventDuration()
                .map(duration -> ResponseEntity.ok(ApiResponse.success(duration)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
