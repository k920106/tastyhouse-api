package com.tastyhouse.webapi.crawling.bbq.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * BBQ 상품 서브 옵션 응답 DTO
 */
@Getter
@Builder
@Schema(description = "BBQ 상품 서브 옵션 응답")
public class BbqProductSubOptionResponse {

    @Schema(description = "서브 옵션 ID", example = "53")
    private Long id;

    @Schema(description = "서브 옵션 제목", example = "뿜치킹 부분육 선택")
    private String subOptionTitle;

    @Schema(description = "필수 선택 개수", example = "1")
    private Integer requiredSelectCount;

    @Schema(description = "최대 선택 개수", example = "1")
    private Integer maxSelectCount;

    @Schema(description = "서브 옵션 아이템 상세 목록")
    private List<SubOptionItemDetailResponse> subOptionItemDetailResponseList;

    @Getter
    @Builder
    @Schema(description = "서브 옵션 아이템 상세 응답")
    public static class SubOptionItemDetailResponse {
        @Schema(description = "아이템 ID", example = "475")
        private Long id;

        @Schema(description = "아이템 제목", example = "한마리")
        private String itemTitle;

        @Schema(description = "추가 가격", example = "0")
        private Integer addPrice;

        @Schema(description = "품절 여부", example = "false")
        private Boolean isSoldOut;

        @Schema(description = "숨김 여부", example = "false")
        private Boolean isHidden;
    }
}
