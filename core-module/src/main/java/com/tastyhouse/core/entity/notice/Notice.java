package com.tastyhouse.core.entity.notice;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NOTICE")
@Getter
@Setter
public class Notice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId; // 매체사 (PK)

    @Column(name = "title", nullable = false)
    private String title; // 제목

    @Column(name = "content", columnDefinition = "TEXT")
    private String content; // 내용

    @Column(name = "is_active", nullable = false)
    private Boolean active = true; // 활성상태

    @Column(name = "is_top", nullable = false)
    private Boolean top = false; // 상단 고정
}
