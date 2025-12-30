package com.tastyhouse.core.entity.review;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "REVIEW_PRODUCT")
public class ReviewProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "product_id", nullable = false)
    private Long productId; // 리뷰에서 선택한 메뉴
}
