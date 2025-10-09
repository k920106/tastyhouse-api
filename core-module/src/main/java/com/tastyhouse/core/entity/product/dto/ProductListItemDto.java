package com.tastyhouse.core.entity.product.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProductListItemDto {
    private final Long id;
    private final Long companyId;
    private final String productCode;
    private final String name;
    private final Long brandId;
    private final Long supplyId;
    private final Integer validityPeriod;
    private final Integer exhibitionPrice;
    private final Integer regularPrice;
    private final Integer supplyPrice;
    private final Boolean isDisplay;
    private final Integer sort;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @QueryProjection
    public ProductListItemDto(Long id, Long companyId, String productCode, String name,
                              Long brandId, Long supplyId, Integer validityPeriod,
                              Integer exhibitionPrice, Integer regularPrice, Integer supplyPrice,
                              Boolean isDisplay, Integer sort, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.companyId = companyId;
        this.productCode = productCode;
        this.name = name;
        this.brandId = brandId;
        this.supplyId = supplyId;
        this.validityPeriod = validityPeriod;
        this.exhibitionPrice = exhibitionPrice;
        this.regularPrice = regularPrice;
        this.supplyPrice = supplyPrice;
        this.isDisplay = isDisplay;
        this.sort = sort;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
