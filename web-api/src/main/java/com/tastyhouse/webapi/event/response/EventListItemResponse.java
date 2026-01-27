package com.tastyhouse.webapi.event.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "이벤트 목록 아이템 응답")
public class EventListItemResponse {

    @Schema(description = "이벤트 ID", example = "1")
    private Long id;

    @Schema(description = "이벤트명", example = "8월 신규회원 특별 할인 이벤트")
    private String name;

    @Schema(description = "이벤트 설명", example = "신규 회원 대상 특별 할인 이벤트입니다.")
    private String description;

    @Schema(description = "이벤트 부제목", example = "어서와, 쿠폰 받자!")
    private String subtitle;

    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
    private String thumbnailImageUrl;

    @Schema(description = "이벤트 타입", example = "RANKING")
    private String type;

    @Schema(description = "이벤트 상태", example = "ACTIVE")
    private String status;

    @Schema(description = "이벤트 시작일시", example = "2020-08-03T00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startAt;

    @Schema(description = "이벤트 종료일시", example = "2020-08-31T23:59:59")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endAt;
}
