package com.tastyhouse.core.repository.review;

import com.tastyhouse.core.entity.review.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewLikeJpaRepository extends JpaRepository<ReviewLike, Long> {

    boolean existsByReviewIdAndMemberId(Long reviewId, Long memberId);

    void deleteByReviewIdAndMemberId(Long reviewId, Long memberId);

    @Query("SELECT rl.reviewId, COUNT(rl) FROM ReviewLike rl WHERE rl.reviewId IN :reviewIds GROUP BY rl.reviewId")
    List<Object[]> countByReviewIdIn(@Param("reviewIds") List<Long> reviewIds);
}
