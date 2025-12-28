package com.tastyhouse.core.entity.review;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "REVIEW_LIKE")
public class ReviewLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    protected ReviewLike() {
    }

    public ReviewLike(Long reviewId, Long memberId) {
        this.reviewId = reviewId;
        this.memberId = memberId;
    }
}
