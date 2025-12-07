package com.tastyhouse.core.entity.banner;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "BANNER")
public class Banner extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 100)
    private String title; // 배너 제목 (관리용)

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "link_url", length = 500)
    private String linkUrl; // 배너 클릭 시 이동할 URL

    @Column(name = "start_date")
    private LocalDateTime startDate; // 시작일자

    @Column(name = "end_date")
    private LocalDateTime endDate; // 종료일자

    @Column(name = "sort", nullable = false)
    private Integer sort; // 우선순위

    @Column(name = "is_active", nullable = false)
    private Boolean active = true; // 활성상태
}
