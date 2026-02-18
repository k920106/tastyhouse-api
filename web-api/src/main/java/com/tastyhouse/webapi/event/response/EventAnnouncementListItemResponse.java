package com.tastyhouse.webapi.event.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "이벤트 당첨자 발표 목록 아이템")
public class EventAnnouncementListItemResponse {

    @Schema(description = "발표 ID", example = "1")
    private Long id;

    @Schema(description = "발표명", example = "8월 신규회원 특별 할인 이벤트 당첨자 발표")
    private String name;

    @Schema(description = "발표 HTML 콘텐츠")
    private String content;

    @Schema(description = "발표일시", example = "2020-09-10T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime announcedAt;
}
