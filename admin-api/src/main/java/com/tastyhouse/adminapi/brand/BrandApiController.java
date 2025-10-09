package com.tastyhouse.adminapi.brand;

import com.tastyhouse.adminapi.product.response.BrandListItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/brands")
public class BrandApiController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<List<BrandListItem>> getBrandList() {
        List<BrandListItem> brands = brandService.findBrandList();
        return ResponseEntity.ok(brands);
    }
}
