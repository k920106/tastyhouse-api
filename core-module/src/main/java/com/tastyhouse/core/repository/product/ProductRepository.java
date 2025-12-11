package com.tastyhouse.core.repository.product;

import com.tastyhouse.core.entity.product.dto.ProductSimpleDto;
import com.tastyhouse.core.entity.product.dto.TodayDiscountProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepository {

    Page<TodayDiscountProductDto> findTodayDiscountProducts(Pageable pageable);

    List<ProductSimpleDto> findProductsByPlaceId(Long placeId);
}
