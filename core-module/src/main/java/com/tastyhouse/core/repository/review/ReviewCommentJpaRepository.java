package com.tastyhouse.core.repository.review;

import com.tastyhouse.core.entity.review.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewCommentJpaRepository extends JpaRepository<ReviewComment, Long> {

    List<ReviewComment> findByReviewIdAndIsHiddenFalseOrderByCreatedAtDesc(Long reviewId);

    @Query("SELECT rc.reviewId, COUNT(rc) FROM ReviewComment rc WHERE rc.reviewId IN :reviewIds AND rc.isHidden = false GROUP BY rc.reviewId")
    List<Object[]> countByReviewIdIn(@Param("reviewIds") List<Long> reviewIds);
}
