package com.tastyhouse.core.entity.report;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "BUG_REPORT_IMAGE",
    indexes = {
        @Index(name = "idx_bug_report_image_bug_report_id", columnList = "bug_report_id")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BugReportImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bug_report_id", nullable = false)
    private Long bugReportId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl; // 이미지 URL

    @Column(name = "sort", nullable = false)
    private Integer sort; // 이미지 정렬 순서

    @Builder
    public BugReportImage(Long bugReportId, String imageUrl, Integer sort) {
        this.bugReportId = bugReportId;
        this.imageUrl = imageUrl;
        this.sort = sort;
    }
}
