package com.tastyhouse.webapi.place;

import com.tastyhouse.core.common.CommonResponse;
import com.tastyhouse.core.common.PagedCommonResponse;
import com.tastyhouse.core.entity.place.Place;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.place.request.PlaceNearRequest;
import com.tastyhouse.webapi.place.response.BestPlaceListItem;
import com.tastyhouse.webapi.place.response.EditorChoiceResponse;
import com.tastyhouse.webapi.place.response.LatestPlaceListItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Place", description = "플레이스 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
public class PlaceApiController {

    private final PlaceService placeService;

    @GetMapping
    public ResponseEntity<CommonResponse<List<Place>>> findAll(@RequestBody PlaceNearRequest placeNearRequest) {
        List<Place> places = placeService.findNearbyPlaces(placeNearRequest.latitude(), placeNearRequest.longitude());
        CommonResponse<List<Place>> response = CommonResponse.success(places);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "베스트 플레이스 목록 조회", description = "평점 기준 베스트 플레이스를 페이징하여 조회합니다. 이미지, 전철역명, 평점, 가게명, 태그 정보를 포함합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = PagedCommonResponse.class)))})
    @GetMapping("/v1/best")
    public ResponseEntity<PagedCommonResponse<BestPlaceListItem>> getBestPlaces(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        PageResult<BestPlaceListItem> pageResult = placeService.findBestPlaces(pageRequest);
        PagedCommonResponse<BestPlaceListItem> response = PagedCommonResponse.success(pageResult.getContent(), page, size, pageResult.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "에디터 초이스 조회", description = "특정 에디터 초이스의 가게 이미지, 제목, 내용, 관련 상품 목록을 조회합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/editor-choice")
    public ResponseEntity<CommonResponse<List<EditorChoiceResponse>>> getEditorChoices(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        List<EditorChoiceResponse> editorChoiceResponses = placeService.findEditorChoices(pageRequest);
        CommonResponse<List<EditorChoiceResponse>> response = CommonResponse.success(editorChoiceResponses);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "최신 플레이스 목록 조회", description = "최근 등록된 플레이스를 페이징하여 조회합니다. 이미지, 전철역명, 평점, 가게명, 태그, 등록일 정보를 포함합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = PagedCommonResponse.class)))})
    @GetMapping("/v1/latest")
    public ResponseEntity<PagedCommonResponse<LatestPlaceListItem>> getLatestPlaces(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        PageResult<LatestPlaceListItem> pageResult = placeService.findLatestPlaces(pageRequest);
        PagedCommonResponse<LatestPlaceListItem> response = PagedCommonResponse.success(pageResult.getContent(), page, size, pageResult.getTotalElements());
        return ResponseEntity.ok(response);
    }
}
