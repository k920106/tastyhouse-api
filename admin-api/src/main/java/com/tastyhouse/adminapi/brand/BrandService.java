package com.tastyhouse.adminapi.brand;

import com.tastyhouse.adminapi.product.response.BrandListItem;
import com.tastyhouse.core.service.BrandCoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandCoreService brandCoreService;

    public List<BrandListItem> findBrandList() {
        return brandCoreService.findAllBrands()
                .stream()
                .map(brand -> new BrandListItem(brand.getId(), brand.getName()))
                .toList();
    }
}
