package com.tastyhouse.webapi.product;

import com.tastyhouse.core.common.PagedApiResponse;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.product.response.TodayDiscountProductItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product", description = "상품 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductApiController {

    private final ProductService productService;

    @Operation(
        summary = "오늘의 할인 상품 목록 조회",
        description = "할인율 기준으로 오늘의 할인 상품을 페이징하여 조회합니다. 상품명, 이미지, 원가, 할인가, 할인율 정보를 포함합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PagedApiResponse.class))
        )
    })
    @GetMapping("/v1/today-discounts")
    public ResponseEntity<PagedApiResponse<TodayDiscountProductItem>> getTodayDiscounts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = new PageRequest(page, size);

        PageResult<TodayDiscountProductItem> pageResult = productService.findTodayDiscountProducts(pageRequest);

        PagedApiResponse<TodayDiscountProductItem> response = PagedApiResponse.success(
            pageResult.getContent(),
            page,
            size,
            pageResult.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }
}
