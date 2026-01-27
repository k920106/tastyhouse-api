package com.tastyhouse.webapi.event;

import com.tastyhouse.core.common.CommonResponse;
import com.tastyhouse.core.entity.event.EventStatus;
import com.tastyhouse.webapi.event.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Event", description = "이벤트 관리 API")
@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventApiController {

    private final EventService eventService;

    @Operation(summary = "진행중인 랭킹 이벤트 기간 조회", description = "현재 진행중인 랭킹 이벤트의 시작일자와 종료일자를 조회합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class))), @ApiResponse(responseCode = "404", description = "진행중인 랭킹 이벤트 없음", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/rank/duration")
    public ResponseEntity<CommonResponse<EventDurationResponse>> getRankingEventDuration() {
        return eventService.getRankingEventDuration().map(duration -> ResponseEntity.ok(CommonResponse.success(duration))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "현재 진행중인 경품 목록 조회", description = "현재 진행중인 프로모션의 등수별 경품 목록을 조회합니다. (1등, 2등, 3등 등)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/rank/prizes")
    public ResponseEntity<CommonResponse<List<PrizeItem>>> getActivePrizeList() {
        List<PrizeItem> prizes = eventService.getActivePrizes();
        CommonResponse<List<PrizeItem>> response = CommonResponse.success(prizes);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이벤트 목록 조회", description = "상태별 이벤트 목록을 조회합니다. (진행중, 종료)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    @GetMapping("/v1/list")
    public ResponseEntity<CommonResponse<List<EventListItemResponse>>> getEventList(
        @Parameter(description = "이벤트 상태 (ACTIVE: 진행중, ENDED: 종료)", example = "ACTIVE")
        @RequestParam EventStatus status
    ) {
        List<EventListItemResponse> events = eventService.getEventList(status);
        return ResponseEntity.ok(CommonResponse.success(events));
    }

    @Operation(summary = "이벤트 상세 조회", description = "이벤트의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    @GetMapping("/v1/{eventId}")
    public ResponseEntity<CommonResponse<EventDetailResponse>> getEventDetail(
        @Parameter(description = "이벤트 ID", example = "1")
        @PathVariable Long eventId
    ) {
        EventDetailResponse event = eventService.getEventDetail(eventId);
        return ResponseEntity.ok(CommonResponse.success(event));
    }

    @Operation(summary = "전체 이벤트 당첨자 발표 목록 조회", description = "모든 이벤트의 당첨자 발표 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    @GetMapping("/v1/winners")
    public ResponseEntity<CommonResponse<List<EventWinnerListResponse>>> getAllEventWinners() {
        List<EventWinnerListResponse> winners = eventService.getAllEventWinners();
        return ResponseEntity.ok(CommonResponse.success(winners));
    }

    @Operation(summary = "특정 이벤트 당첨자 발표 조회", description = "특정 이벤트의 당첨자 발표를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    @GetMapping("/v1/winners/{eventId}")
    public ResponseEntity<CommonResponse<EventWinnerListResponse>> getEventWinners(
        @Parameter(description = "이벤트 ID", example = "1")
        @PathVariable Long eventId
    ) {
        EventWinnerListResponse winners = eventService.getEventWinners(eventId);
        return ResponseEntity.ok(CommonResponse.success(winners));
    }
}
