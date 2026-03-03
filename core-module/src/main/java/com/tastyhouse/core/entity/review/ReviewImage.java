package com.tastyhouse.core.entity.review;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(
    name = "REVIEW_IMAGE",
    indexes = {
        @Index(name = "idx_review_image_review_id", columnList = "review_id")
    }
)
public class ReviewImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "uploaded_file_id", nullable = false)
    private Long uploadedFileId; // UploadedFile PK

    @Column(name = "sort", nullable = false)
    private Integer sort; // 이미지 정렬 순서

    @Builder
    public ReviewImage(Long reviewId, Long uploadedFileId, Integer sort) {
        this.reviewId = reviewId;
        this.uploadedFileId = uploadedFileId;
        this.sort = sort;
    }
}
