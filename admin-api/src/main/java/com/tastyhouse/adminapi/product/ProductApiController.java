package com.tastyhouse.adminapi.product;

import com.tastyhouse.adminapi.common.PageRequest;
import com.tastyhouse.adminapi.common.PageResult;
import com.tastyhouse.adminapi.product.request.ProductListQuery;
import com.tastyhouse.adminapi.product.request.ProductSyncRequest;
import com.tastyhouse.adminapi.product.response.ProductBulkUpdateResponse;
import com.tastyhouse.adminapi.product.response.ProductListItem;
import com.tastyhouse.core.common.ApiResponse;
import com.tastyhouse.core.common.PagedApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductApiController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PagedApiResponse<ProductListItem>> getProductList(@ModelAttribute ProductListQuery query) {
        PageRequest pageRequest = new PageRequest(query.getPage(), query.getSize());

        PageResult<ProductListItem> pageResult = productService.findProductList(
            query.getCompanyId(),
            query.getProductCode(),
            query.getName(),
            query.getBrandId(),
            query.getSupplyId(),
            query.getDisplay(),
            pageRequest
        );

        PagedApiResponse<ProductListItem> response = PagedApiResponse.success(
            pageResult.getContent(),
            query.getPage(),
            query.getSize(),
            pageResult.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<Void>> syncProducts(@RequestBody ProductSyncRequest request) {
        // TODO: 외부 API 호출 후 상품 리스트 동기화 로직 구현 예정
        ApiResponse<Void> response = ApiResponse.success(null, "OK");
//        ApiResponse<Void> response = ApiResponse.error("상품 동기화 중 오류가 발생하였습니다.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bulk/template/download")
    public ResponseEntity<Resource> downloadTemplate() {
        Resource resource = new ClassPathResource("test.xlsx");

//        return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"product_template.xlsx\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(resource);
    }

    @PatchMapping("/bulk/update")
    public ResponseEntity<ApiResponse<ProductBulkUpdateResponse>> updateProducts(@RequestParam("file") MultipartFile file) {
        // TODO: 엑셀 파일 업로드 후 상품 일괄 수정 로직 구현 예정
        ApiResponse<ProductBulkUpdateResponse> response = ApiResponse.success(ProductBulkUpdateResponse.builder().count(1).build(), "OK");
//        ApiResponse<ProductBulkUpdateResponse> response = ApiResponse.error("상품 정보 일괄 변경 중 오류가 발생하였습니다.");
        return ResponseEntity.ok(response);
    }
}
