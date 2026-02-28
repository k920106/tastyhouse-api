package com.tastyhouse.core.entity.review;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "REVIEW_TAG")
public class ReviewTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    public ReviewTag(Long reviewId, Long tagId) {
        this.reviewId = reviewId;
        this.tagId = tagId;
    }
}
