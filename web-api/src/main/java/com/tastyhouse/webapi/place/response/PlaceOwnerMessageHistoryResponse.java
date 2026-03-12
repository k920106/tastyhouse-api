package com.tastyhouse.webapi.place.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "플레이스 사장님 한마디 히스토리 응답")
public record PlaceOwnerMessageHistoryResponse(
        @Schema(description = "사장님 한마디", example = "사장님의 한마디는 환영의 노래입니다...")
        String message,

        @Schema(description = "생성일시", example = "2024-01-01T12:00:00")
        LocalDateTime createdAt
) {
}
