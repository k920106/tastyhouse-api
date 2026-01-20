package com.tastyhouse.webapi.report;

import com.tastyhouse.core.common.CommonResponse;
import com.tastyhouse.webapi.report.request.PlaceReportCreateRequest;
import com.tastyhouse.webapi.report.response.PlaceReportResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "PlaceReport", description = "맛집 제보 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place-reports")
public class PlaceReportApiController {

    private final PlaceReportService placeReportService;

    @Operation(summary = "맛집 제보", description = "맛집을 제보합니다. 상호명과 주소 정보(기본 주소, 상세 주소)를 포함합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "제보 성공", content = @Content(schema = @Schema(implementation = PlaceReportResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검증 실패)")
    })
    @PostMapping("/v1")
    public ResponseEntity<CommonResponse<PlaceReportResponse>> createPlaceReport(
        @Valid @RequestBody PlaceReportCreateRequest request
    ) {
        PlaceReportResponse response = placeReportService.createPlaceReport(request);
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
