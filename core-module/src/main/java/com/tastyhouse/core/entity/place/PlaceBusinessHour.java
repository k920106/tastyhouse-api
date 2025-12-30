package com.tastyhouse.core.entity.place;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Entity
@Table(name = "PLACE_BUSINESS_HOUR")
public class PlaceBusinessHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_type", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private DayType dayType; // 요일 타입 (평일, 토요일, 일요일, 공휴일)

    @Column(name = "open_time")
    private LocalTime openTime; // 오픈 시간

    @Column(name = "close_time")
    private LocalTime closeTime; // 마감 시간

    @Column(name = "break_start_time")
    private LocalTime breakStartTime; // 브레이크타임 시작

    @Column(name = "break_end_time")
    private LocalTime breakEndTime; // 브레이크타임 종료

    @Column(name = "is_closed")
    private Boolean isClosed; // 해당 요일 휴무 여부
}
