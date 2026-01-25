package com.tastyhouse.webapi.product.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@Schema(description = "상품 상세 정보")
public class ProductDetailResponse {

    @Schema(description = "상품 ID", example = "1")
    private Long id;

    @Schema(description = "플레이스 ID", example = "100")
    private Long placeId;

    @Schema(description = "플레이스명", example = "맛있는 파스타집")
    private String placeName;

    @Schema(description = "상품명", example = "명란 크림 파스타")
    private String name;

    @Schema(description = "상품 설명", example = "신선한 명란과 크림소스의 조화")
    private String description;

    @Schema(description = "이미지 URL 목록", example = "[\"https://example.com/product1.jpg\", \"https://example.com/product2.jpg\"]")
    private List<String> imageUrls;

    @Schema(description = "원가", example = "18500")
    private Integer originalPrice;

    @Schema(description = "할인가", example = "16650")
    private Integer discountPrice;

    @Schema(description = "할인율", example = "10.00")
    private BigDecimal discountRate;

    @Schema(description = "상품 평점", example = "4.5")
    private Double rating;

    @Schema(description = "리뷰 수", example = "128")
    private Integer reviewCount;

    @Schema(description = "대표 메뉴 여부", example = "true")
    private Boolean isRepresentative;

    @Schema(description = "품절 여부", example = "false")
    private Boolean isSoldOut;

    @Schema(description = "카테고리명", example = "시그니처 메뉴")
    private String categoryName;

    @Schema(description = "옵션 그룹 목록")
    private List<OptionGroupResponse> optionGroups;

    @Getter
    @Builder
    @Schema(description = "옵션 그룹")
    public static class OptionGroupResponse {

        @Schema(description = "옵션 그룹 ID", example = "1")
        private Long id;

        @Schema(description = "옵션 그룹명", example = "맵기 선택")
        private String name;

        @Schema(description = "옵션 그룹 설명", example = "원하시는 맵기를 선택해주세요")
        private String description;

        @Schema(description = "필수 선택 여부", example = "true")
        private Boolean isRequired;

        @Schema(description = "복수 선택 가능 여부", example = "false")
        private Boolean isMultipleSelect;

        @Schema(description = "최소 선택 개수", example = "1")
        private Integer minSelect;

        @Schema(description = "최대 선택 개수", example = "1")
        private Integer maxSelect;

        @Schema(description = "공통 옵션 여부", example = "false")
        private Boolean isCommon;

        @Schema(description = "옵션 목록")
        private List<OptionResponse> options;
    }

    @Getter
    @Builder
    @Schema(description = "옵션")
    public static class OptionResponse {

        @Schema(description = "옵션 ID", example = "1")
        private Long id;

        @Schema(description = "옵션명", example = "많이 맵게")
        private String name;

        @Schema(description = "추가 금액", example = "0")
        private Integer additionalPrice;

        @Schema(description = "품절 여부", example = "false")
        private Boolean isSoldOut;
    }
}
