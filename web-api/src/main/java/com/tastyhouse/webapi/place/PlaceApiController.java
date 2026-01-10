package com.tastyhouse.webapi.place;

import com.tastyhouse.core.common.CommonResponse;
import com.tastyhouse.core.common.PagedCommonResponse;
import com.tastyhouse.core.entity.place.Amenity;
import com.tastyhouse.core.entity.place.FoodType;
import com.tastyhouse.core.entity.place.Place;
import com.tastyhouse.core.entity.place.PlaceImageCategory;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.place.request.LatestPlaceFilterRequest;
import com.tastyhouse.webapi.place.request.PlaceNearRequest;
import com.tastyhouse.webapi.place.response.*;
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

    @Operation(summary = "테하 초이스 조회", description = "특정 테하 초이스의 가게 이미지, 제목, 내용, 관련 상품 목록을 조회합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/editor-choice")
    public ResponseEntity<CommonResponse<List<EditorChoiceResponse>>> getEditorChoices(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        List<EditorChoiceResponse> editorChoiceResponses = placeService.findEditorChoices(pageRequest);
        CommonResponse<List<EditorChoiceResponse>> response = CommonResponse.success(editorChoiceResponses);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "최신 플레이스 목록 조회", description = "최근 등록된 플레이스를 페이징하여 조회합니다. 이미지, 전철역명, 평점, 가게명, 태그, 등록일 정보를 포함합니다. 전철역, 음식종류, 편의시설 필터를 적용할 수 있습니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = PagedCommonResponse.class)))})
    @GetMapping("/v1/latest")
    public ResponseEntity<PagedCommonResponse<LatestPlaceListItem>> getLatestPlaces(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) Long stationId, @RequestParam(required = false) List<FoodType> foodTypes, @RequestParam(required = false) List<Amenity> amenities) {
        PageRequest pageRequest = new PageRequest(page, size);
        LatestPlaceFilterRequest filterRequest = new LatestPlaceFilterRequest(stationId, foodTypes, amenities);
        PageResult<LatestPlaceListItem> pageResult = placeService.findLatestPlaces(pageRequest, filterRequest);
        PagedCommonResponse<LatestPlaceListItem> response = PagedCommonResponse.success(pageResult.getContent(), page, size, pageResult.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "전철역 목록 조회", description = "전철역 목록을 가나다라 순으로 조회합니다. ID와 전철역명을 반환합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/stations")
    public ResponseEntity<CommonResponse<List<StationListItem>>> getStations() {
        List<StationListItem> stations = placeService.findAllStations();
        CommonResponse<List<StationListItem>> response = CommonResponse.success(stations);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "음식종류 목록 조회", description = "음식종류 전체 목록을 조회합니다. 코드와 표시명을 반환합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/food-types")
    public ResponseEntity<CommonResponse<List<FoodTypeListItem>>> getFoodTypes() {
        List<FoodTypeListItem> foodTypes = placeService.findAllFoodTypes();
        CommonResponse<List<FoodTypeListItem>> response = CommonResponse.success(foodTypes);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "편의시설 목록 조회", description = "편의시설 전체 목록을 조회합니다. 코드와 표시명을 반환합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/amenities")
    public ResponseEntity<CommonResponse<List<AmenityListItem>>> getAmenities() {
        List<AmenityListItem> amenities = placeService.findAllAmenities();
        CommonResponse<List<AmenityListItem>> response = CommonResponse.success(amenities);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요약 정보 조회", description = "플레이스의 요약 정보를 조회합니다. 상호명, 도로명 주소, 지번 주소, 총 평점을 포함합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/{placeId}/summary")
    public ResponseEntity<CommonResponse<PlaceSummaryResponse>> getPlaceSummary(@PathVariable Long placeId) {
        PlaceSummaryResponse placeSummary = placeService.getPlaceSummary(placeId);
        CommonResponse<PlaceSummaryResponse> response = CommonResponse.success(placeSummary);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "정보 조회", description = "플레이스의 기본 정보를 조회합니다. 운영시간, 전화번호 등을 포함합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/{placeId}/info")
    public ResponseEntity<CommonResponse<PlaceInfoResponse>> getPlaceInfo(@PathVariable Long placeId) {
        PlaceInfoResponse placeInfo = placeService.getPlaceInfo(placeId);
        CommonResponse<PlaceInfoResponse> response = CommonResponse.success(placeInfo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "썸네일 이미지 조회", description = "플레이스의 썸네일 이미지 목록을 조회합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/{placeId}/thumbnails")
    public ResponseEntity<CommonResponse<List<PlaceThumbnailResponse>>> getPlaceThumbnails(@PathVariable Long placeId) {
        List<PlaceThumbnailResponse> thumbnails = placeService.getPlaceThumbnails(placeId);
        CommonResponse<List<PlaceThumbnailResponse>> response = CommonResponse.success(thumbnails);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "메뉴 목록 조회", description = "플레이스의 메뉴 목록을 조회합니다. 대표 메뉴가 우선 표시됩니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/{placeId}/menus")
    public ResponseEntity<CommonResponse<List<PlaceMenuResponse>>> getPlaceMenus(@PathVariable Long placeId) {
        List<PlaceMenuResponse> menus = placeService.getPlaceMenus(placeId);
        CommonResponse<List<PlaceMenuResponse>> response = CommonResponse.success(menus);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사진 목록 조회", description = "플레이스의 사진 목록을 조회합니다. 카테고리별로 필터링할 수 있습니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = PagedCommonResponse.class)))})
    @GetMapping("/v1/{placeId}/photos")
    public ResponseEntity<PagedCommonResponse<PlacePhotoResponse>> getPlacePhotos(@PathVariable Long placeId, @RequestParam(required = false) PlaceImageCategory category, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        PageResult<PlacePhotoResponse> pageResult = placeService.getPlacePhotos(placeId, category, pageRequest);
        PagedCommonResponse<PlacePhotoResponse> response = PagedCommonResponse.success(pageResult.getContent(), page, size, pageResult.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "플레이스 리뷰 목록 조회", description = "플레이스의 리뷰 목록을 조회합니다. 평점별로 필터링할 수 있습니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = PagedCommonResponse.class)))})
    @GetMapping("/v1/{placeId}/reviews")
    public ResponseEntity<PagedCommonResponse<PlaceReviewResponse>> getPlaceReviews(@PathVariable Long placeId, @RequestParam(required = false) Integer rating, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        PageResult<PlaceReviewResponse> pageResult = placeService.getPlaceReviews(placeId, rating, pageRequest);
        PagedCommonResponse<PlaceReviewResponse> response = PagedCommonResponse.success(pageResult.getContent(), page, size, pageResult.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "리뷰 통계 조회", description = "플레이스의 리뷰 통계를 조회합니다. 평점, 카테고리별 점수, 재방문의사 등을 포함합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/{placeId}/reviews/statistics")
    public ResponseEntity<CommonResponse<PlaceReviewStatisticsResponse>> getPlaceReviewStatistics(@PathVariable Long placeId) {
        PlaceReviewStatisticsResponse statistics = placeService.getPlaceReviewStatistics(placeId);
        CommonResponse<PlaceReviewStatisticsResponse> response = CommonResponse.success(statistics);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "북마크 여부 조회", description = "플레이스가 현재 사용자에 의해 북마크되었는지 여부를 조회합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공")})
    @GetMapping("/v1/{placeId}/bookmark")
    public ResponseEntity<CommonResponse<PlaceBookmarkResponse>> isBookmarked(@PathVariable Long placeId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        PlaceBookmarkResponse bookmarked;
        if (userDetails == null) {
            bookmarked = new PlaceBookmarkResponse(false);
        } else {
            Long memberId = userDetails.getMemberId();
            bookmarked = placeService.isBookmarked(placeId, memberId);
        }
        return ResponseEntity.ok(CommonResponse.success(bookmarked));
    }

    @Operation(summary = "북마크 토글", description = "플레이스에 대한 북마크를 추가하거나 제거합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "처리 성공")})
    @PostMapping("/v1/{placeId}/bookmark")
    public ResponseEntity<CommonResponse<PlaceBookmarkResponse>> toggleBookmark(@PathVariable Long placeId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        boolean bookmarked = placeService.toggleBookmark(placeId, userDetails.getMemberId());
        return ResponseEntity.ok(CommonResponse.success(new PlaceBookmarkResponse(bookmarked)));
    }

    @Operation(summary = "사장님 한마디", description = "플레이스의 가장 최근 사장님 한마디 히스토리를 조회합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/{placeId}/owner-message-history")
    public ResponseEntity<CommonResponse<PlaceOwnerMessageHistoryResponse>> getPlaceOwnerMessageHistory(@PathVariable Long placeId) {
        PlaceOwnerMessageHistoryResponse history = placeService.getPlaceOwnerMessageHistory(placeId);
        CommonResponse<PlaceOwnerMessageHistoryResponse> response = CommonResponse.success(history);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "상호명 조회", description = "플레이스의 상호명을 조회합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    @GetMapping("/v1/{placeId}/name")
    public ResponseEntity<CommonResponse<PlaceNameResponse>> getPlaceName(@PathVariable Long placeId) {
        PlaceNameResponse placeName = placeService.getPlaceName(placeId);
        CommonResponse<PlaceNameResponse> response = CommonResponse.success(placeName);
        return ResponseEntity.ok(response);
    }
}
