package com.tastyhouse.core.repository.product;

import com.tastyhouse.core.entity.product.dto.TodayDiscountProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

    Page<TodayDiscountProductDto> findTodayDiscountProducts(Pageable pageable);
}
