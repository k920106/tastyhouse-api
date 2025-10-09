package com.tastyhouse.core.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedApiResponse<T> {
    private boolean success;
    private List<T> data;
    private PageInfo pagination;
    private String message;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
    }

    public static <T> PagedApiResponse<T> success(List<T> data, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        PageInfo pageInfo = new PageInfo(page, size, totalElements, totalPages);
        return new PagedApiResponse<>(true, data, pageInfo, null);
    }

    public static <T> PagedApiResponse<T> success(List<T> data, int page, int size, long totalElements, String message) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        PageInfo pageInfo = new PageInfo(page, size, totalElements, totalPages);
        return new PagedApiResponse<>(true, data, pageInfo, message);
    }

    public static <T> PagedApiResponse<T> error(String message) {
        return new PagedApiResponse<>(false, null, null, message);
    }
}