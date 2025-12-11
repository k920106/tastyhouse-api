package com.tastyhouse.webapi.product;

import com.tastyhouse.core.entity.product.dto.TodayDiscountProductDto;
import com.tastyhouse.core.service.ProductCoreService;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.product.response.TodayDiscountProductItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductCoreService productCoreService;

    public PageResult<TodayDiscountProductItem> findTodayDiscountProducts(PageRequest pageRequest) {
        ProductCoreService.TodayDiscountProductPageResult coreResult = productCoreService.findTodayDiscountProducts(
            pageRequest.getPage(), pageRequest.getSize()
        );

        List<TodayDiscountProductItem> todayDiscountProductItems = coreResult.getContent().stream()
            .map(this::convertToTodayDiscountProductItem)
            .toList();

        return new PageResult<>(
            todayDiscountProductItems,
            coreResult.getTotalElements(),
            coreResult.getTotalPages(),
            coreResult.getCurrentPage(),
            coreResult.getPageSize()
        );
    }

    private TodayDiscountProductItem convertToTodayDiscountProductItem(TodayDiscountProductDto dto) {
        return TodayDiscountProductItem.builder()
            .id(dto.getId())
            .placeName(dto.getPlaceName())
            .name(dto.getName())
            .imageUrl(dto.getImageUrl())
            .originalPrice(dto.getOriginalPrice())
            .discountPrice(dto.getDiscountPrice())
            .discountRate(dto.getDiscountRate())
            .build();
    }
}
