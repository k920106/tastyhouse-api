package com.tastyhouse.adminapi.product.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductBulkUpdateResponse {
    private int count;
}
