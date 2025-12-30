package com.tastyhouse.core.repository.review;

import com.tastyhouse.core.entity.review.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageJpaRepository extends JpaRepository<ReviewImage, Long> {

    List<ReviewImage> findByReviewIdOrderBySortAsc(Long reviewId);

    List<ReviewImage> findByReviewIdInOrderBySortAsc(List<Long> reviewIds);
}
