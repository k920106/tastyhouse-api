package com.tastyhouse.core.repository.review;

import com.tastyhouse.core.entity.rank.dto.MemberReviewCountDto;
import com.tastyhouse.core.entity.review.dto.BestReviewListItemDto;
import com.tastyhouse.core.entity.review.dto.LatestReviewListItemDto;
import com.tastyhouse.core.entity.review.dto.ReviewDetailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    Page<BestReviewListItemDto> findBestReviews(Pageable pageable);

    Page<LatestReviewListItemDto> findLatestReviews(Pageable pageable);

    Page<LatestReviewListItemDto> findLatestReviewsByFollowing(List<Long> followingMemberIds, Pageable pageable);

    Page<LatestReviewListItemDto> findLatestReviewsByPlaceId(Long placeId, Integer rating, Pageable pageable, Boolean hasImage, String sortType);

    List<LatestReviewListItemDto> findReviewsByPlaceIdAndRating(Long placeId, Integer rating, int limit);

    Page<LatestReviewListItemDto> findLatestReviewsByProductId(Long productId, Integer rating, Pageable pageable, Boolean hasImage, String sortType);

    List<LatestReviewListItemDto> findReviewsByProductIdAndRating(Long productId, Integer rating, int limit);

    List<MemberReviewCountDto> countReviewsByMemberWithPeriod(
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    Optional<ReviewDetailDto> findReviewDetail(Long reviewId);
}
