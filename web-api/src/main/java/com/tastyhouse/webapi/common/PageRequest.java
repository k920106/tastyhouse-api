package com.tastyhouse.webapi.common;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageRequest {

    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
    private int page;

    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    private int size;
}
