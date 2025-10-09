package com.tastyhouse.core.repository.product;

import com.tastyhouse.core.entity.product.dto.ProductListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

    Page<ProductListItemDto> findAllWithFilterAsDto(Long companyId, String productCode, String name,
                                                     Long brandId, Long supplyId, Boolean display, Pageable pageable);
}
