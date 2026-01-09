package com.tastyhouse.webapi.place.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@Schema(description = "플레이스 기본 정보 응답")
public class PlaceInfoResponse {

    @Schema(description = "플레이스 ID", example = "1")
    private Long id;

    @Schema(description = "위도", example = "37.5234")
    private BigDecimal latitude;

    @Schema(description = "경도", example = "127.0234")
    private BigDecimal longitude;

    @Schema(description = "전철역명", example = "신사역")
    private String stationName;

    @Schema(description = "전화번호", example = "02-1234-5678")
    private String phoneNumber;

    @Schema(description = "휴무일 목록")
    private List<ClosedDayItem> closedDays;

    @Schema(description = "운영시간 목록")
    private List<BusinessHourItem> businessHours;

    @Schema(description = "브레이크타임 목록")
    private List<BreakTimeItem> breakTimes;

    @Getter
    @Builder
    @Schema(description = "운영시간 정보")
    public static class BusinessHourItem {

        @Schema(description = "요일 타입", example = "WEEKDAY")
        private String dayType;

        @Schema(description = "요일 타입 설명", example = "평일")
        private String dayTypeDescription;

        @Schema(description = "오픈 시간", example = "11:00")
        private String openTime;

        @Schema(description = "마감 시간", example = "22:00")
        private String closeTime;

        @Schema(description = "휴무 여부", example = "false")
        private Boolean isClosed;
    }

    @Getter
    @Builder
    @Schema(description = "브레이크타임 정보")
    public static class BreakTimeItem {
        @Schema(description = "요일 타입", example = "WEEKDAY")
        private String dayType;

        @Schema(description = "요일 타입 설명", example = "평일")
        private String dayTypeDescription;

        @Schema(description = "브레이크타임 시작", example = "15:00")
        private String startTime;

        @Schema(description = "브레이크타임 종료", example = "17:00")
        private String endTime;
    }

    @Getter
    @Builder
    @Schema(description = "휴무일 정보")
    public static class ClosedDayItem {
        @Schema(description = "휴무일 타입", example = "EVERY_WEEK_MONDAY")
        private String closedDayType;

        @Schema(description = "휴무일 설명", example = "매주 월요일")
        private String description;
    }
}
