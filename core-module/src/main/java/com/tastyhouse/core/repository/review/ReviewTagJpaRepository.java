package com.tastyhouse.core.repository.review;

import com.tastyhouse.core.entity.review.ReviewTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewTagJpaRepository extends JpaRepository<ReviewTag, Long> {

    @Query("SELECT rt.tagId FROM ReviewTag rt WHERE rt.reviewId = :reviewId")
    List<Long> findTagIdsByReviewId(@Param("reviewId") Long reviewId);

    void deleteByReviewId(Long reviewId);
}
