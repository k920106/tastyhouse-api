package com.tastyhouse.core.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(true, data, null);
    }

    public static <T> CommonResponse<T> success(T data, String message) {
        return new CommonResponse<>(true, data, message);
    }

    public static <T> CommonResponse<T> error(String message) {
        return new CommonResponse<>(false, null, message);
    }

    public static <T> CommonResponse<T> error(ApiError error) {
        return new CommonResponse<>(false, null, error.getMessage());
    }
}
