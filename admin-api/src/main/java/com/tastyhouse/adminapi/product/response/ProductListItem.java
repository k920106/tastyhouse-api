package com.tastyhouse.adminapi.product.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductListItem {
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
    private final Boolean display;
    private final Integer sort;
}
