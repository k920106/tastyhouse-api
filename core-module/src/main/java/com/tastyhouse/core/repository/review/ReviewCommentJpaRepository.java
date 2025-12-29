package com.tastyhouse.core.repository.review;

import com.tastyhouse.core.entity.review.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewCommentJpaRepository extends JpaRepository<ReviewComment, Long> {

    List<ReviewComment> findByReviewIdAndIsHiddenFalseOrderByCreatedAtDesc(Long reviewId);
}
