package com.tastyhouse.webapi.event.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "이벤트 상세 응답")
public class EventDetailResponse {

    @Schema(description = "배너 이미지 URL", example = "https://example.com/banner.jpg")
    private String bannerImageUrl;
}
