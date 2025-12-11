package com.tastyhouse.webapi.place;

import com.tastyhouse.core.common.ApiResponse;
import com.tastyhouse.core.common.PagedApiResponse;
import com.tastyhouse.core.entity.place.Place;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.place.request.PlaceNearRequest;
import com.tastyhouse.webapi.place.response.BestPlaceListItem;
import com.tastyhouse.webapi.place.response.EditorChoiceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
    public ResponseEntity<ApiResponse<List<Place>>> findAll(@RequestBody PlaceNearRequest placeNearRequest) {
        List<Place> places = placeService.findNearbyPlaces(placeNearRequest.latitude(), placeNearRequest.longitude());
        ApiResponse<List<Place>> response = ApiResponse.success(places);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "베스트 플레이스 목록 조회",
        description = "평점 기준 베스트 플레이스를 페이징하여 조회합니다. 이미지, 전철역명, 평점, 가게명, 태그 정보를 포함합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PagedApiResponse.class))
        )
    })
    @GetMapping("/v1/best")
    public ResponseEntity<PagedApiResponse<BestPlaceListItem>> getBestPlaces(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
    ) {
        PageRequest pageRequest = new PageRequest(page, size);

        PageResult<BestPlaceListItem> pageResult = placeService.findBestPlaces(pageRequest);

        PagedApiResponse<BestPlaceListItem> response = PagedApiResponse.success(
            pageResult.getContent(),
            page,
            size,
            pageResult.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "에디터 초이스 조회",
        description = "특정 에디터 초이스의 가게 이미지, 제목, 내용, 관련 상품 목록을 조회합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/v1/editor-choice")
    public ResponseEntity<ApiResponse<List<EditorChoiceResponse>>> getEditorChoices() {
        List<EditorChoiceResponse> editorChoiceResponses = placeService.findEditorChoices();
        ApiResponse<List<EditorChoiceResponse>> response = ApiResponse.success(editorChoiceResponses);
        return ResponseEntity.ok(response);
    }
}
