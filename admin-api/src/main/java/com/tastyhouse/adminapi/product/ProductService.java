package com.tastyhouse.adminapi.product;

import com.tastyhouse.adminapi.common.PageRequest;
import com.tastyhouse.adminapi.common.PageResult;
import com.tastyhouse.adminapi.product.response.ProductListItem;
import com.tastyhouse.core.entity.product.dto.ProductListItemDto;
import com.tastyhouse.core.service.ProductCoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductCoreService productCoreService;

    public PageResult<ProductListItem> findProductList(Long companyId, String productCode, String name,
                                                      Long brandId, Long supplyId, Boolean display, PageRequest pageRequest) {
        ProductCoreService.ProductListItemPageResult coreResult = productCoreService.findProductListItems(
            companyId, productCode, name, brandId, supplyId, display,
            pageRequest.getPage(), pageRequest.getSize()
        );

        List<ProductListItem> productListItems = coreResult.getContent().stream()
            .map(this::convertToProductListItem)
            .toList();

        return new PageResult<>(
            productListItems,
            coreResult.getTotalElements(),
            coreResult.getTotalPages(),
            coreResult.getCurrentPage(),
            coreResult.getPageSize()
        );
    }

    private ProductListItem convertToProductListItem(ProductListItemDto dto) {
        return ProductListItem.builder()
            .id(dto.getId())
            .companyId(dto.getCompanyId())
            .productCode(dto.getProductCode())
            .name(dto.getName())
            .brandId(dto.getBrandId())
            .supplyId(dto.getSupplyId())
            .validityPeriod(dto.getValidityPeriod())
            .exhibitionPrice(dto.getExhibitionPrice())
            .regularPrice(dto.getRegularPrice())
            .supplyPrice(dto.getSupplyPrice())
            .display(dto.getDisplay())
            .sort(dto.getSort())
            .build();
    }
}
