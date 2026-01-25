package com.tastyhouse.core.repository.review;

import com.tastyhouse.core.entity.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {

    Page<Review> findByPlaceIdAndIsHiddenFalseOrderByCreatedAtDesc(Long placeId, Pageable pageable);

    Page<Review> findByPlaceIdAndTotalRatingAndIsHiddenFalseOrderByCreatedAtDesc(Long placeId, Double rating, Pageable pageable);

    Long countByPlaceIdAndIsHiddenFalse(Long placeId);

    @Query("SELECT AVG(r.tasteRating) FROM Review r WHERE r.placeId = :placeId AND r.isHidden = false")
    Double getAverageTasteRating(@Param("placeId") Long placeId);

    @Query("SELECT AVG(r.amountRating) FROM Review r WHERE r.placeId = :placeId AND r.isHidden = false")
    Double getAverageAmountRating(@Param("placeId") Long placeId);

    @Query("SELECT AVG(r.priceRating) FROM Review r WHERE r.placeId = :placeId AND r.isHidden = false")
    Double getAveragePriceRating(@Param("placeId") Long placeId);

    @Query("SELECT AVG(r.atmosphereRating) FROM Review r WHERE r.placeId = :placeId AND r.isHidden = false")
    Double getAverageAtmosphereRating(@Param("placeId") Long placeId);

    @Query("SELECT AVG(r.kindnessRating) FROM Review r WHERE r.placeId = :placeId AND r.isHidden = false")
    Double getAverageKindnessRating(@Param("placeId") Long placeId);

    @Query("SELECT AVG(r.hygieneRating) FROM Review r WHERE r.placeId = :placeId AND r.isHidden = false")
    Double getAverageHygieneRating(@Param("placeId") Long placeId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.placeId = :placeId AND r.isHidden = false AND r.willRevisit = true")
    Long countWillRevisit(@Param("placeId") Long placeId);

    @Query("SELECT MONTH(r.createdAt) as month, COUNT(r) as count FROM Review r WHERE r.placeId = :placeId AND r.isHidden = false AND YEAR(r.createdAt) = :year GROUP BY MONTH(r.createdAt)")
    Object[][] getMonthlyReviewCounts(@Param("placeId") Long placeId, @Param("year") int year);

    @Query("SELECT FLOOR(r.totalRating) as rating, COUNT(r) as count FROM Review r WHERE r.placeId = :placeId AND r.isHidden = false GROUP BY FLOOR(r.totalRating)")
    Object[][] getRatingCounts(@Param("placeId") Long placeId);

    // 상품 리뷰 조회
    Page<Review> findByProductIdAndIsHiddenFalseOrderByCreatedAtDesc(Long productId, Pageable pageable);

    Page<Review> findByProductIdAndTotalRatingAndIsHiddenFalseOrderByCreatedAtDesc(Long productId, Double rating, Pageable pageable);

    Long countByProductIdAndIsHiddenFalse(Long productId);

    // 상품 리뷰 통계
    @Query("SELECT AVG(r.tasteRating) FROM Review r WHERE r.productId = :productId AND r.isHidden = false")
    Double getAverageTasteRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT AVG(r.amountRating) FROM Review r WHERE r.productId = :productId AND r.isHidden = false")
    Double getAverageAmountRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT AVG(r.priceRating) FROM Review r WHERE r.productId = :productId AND r.isHidden = false")
    Double getAveragePriceRatingByProductId(@Param("productId") Long productId);
}