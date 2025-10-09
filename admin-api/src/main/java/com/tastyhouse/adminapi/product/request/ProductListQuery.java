package com.tastyhouse.adminapi.product.request;

import lombok.Data;

@Data
public class ProductListQuery {
    private int page = 0;
    private int size = 10;
    private Long companyId;
    private String productCode;
    private String name;
    private Long brandId;
    private Long supplyId;
    private Boolean display;
}
