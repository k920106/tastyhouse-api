package com.tastyhouse.adminapi.product.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataTablesResponse<T> {
    private Pagination pagination;  // draw, total, filtered 묶음
    private List<T> data;
}
