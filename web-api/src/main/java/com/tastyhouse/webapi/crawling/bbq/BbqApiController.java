package com.tastyhouse.webapi.crawling.bbq;

import com.tastyhouse.core.common.CommonResponse;
import com.tastyhouse.webapi.crawling.bbq.response.BbqProductCategoryResponse;
import com.tastyhouse.webapi.crawling.bbq.response.BbqProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * BBQ API 컨트롤러
 *
 * BBQ 외부 API를 호출하는 엔드포인트를 제공합니다.
 */
@Tag(name = "BBQ", description = "BBQ 외부 API 연동")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crawling/bbq")
public class BbqApiController {

    private final BbqService bbqService;

    @Operation(summary = "BBQ 메뉴 카테고리 목록 조회", description = "BBQ의 메뉴 카테고리 목록을 조회합니다. ProductCategory Entity 구조에 맞춰 반환됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "500", description = "BBQ API 호출 실패")
    })
    @GetMapping("/v1/menu/categories")
    public ResponseEntity<CommonResponse<List<BbqProductCategoryResponse>>> getMenuCategories() {
        List<BbqProductCategoryResponse> categories = bbqService.getMenuCategories();
        return ResponseEntity.ok(CommonResponse.success(categories));
    }

    @Operation(summary = "BBQ 카테고리별 메뉴 목록 조회", description = "BBQ의 특정 카테고리에 속한 메뉴 목록을 조회합니다. Product Entity 구조에 맞춰 반환됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "500", description = "BBQ API 호출 실패")
    })
    @GetMapping("/v1/menu/categories/{categoryId}/menus")
    public ResponseEntity<CommonResponse<List<BbqProductResponse>>> getMenusByCategoryId(
            @Parameter(description = "카테고리 ID", example = "7", required = true)
            @PathVariable Long categoryId) {
        List<BbqProductResponse> menus = bbqService.getMenusByCategoryId(categoryId);
        return ResponseEntity.ok(CommonResponse.success(menus));
    }
}
