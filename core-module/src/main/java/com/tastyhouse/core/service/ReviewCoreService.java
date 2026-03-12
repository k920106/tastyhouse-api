package com.tastyhouse.core.service;

import com.tastyhouse.core.common.PageResult;
import com.tastyhouse.core.common.ReviewsByRatingResult;
import com.tastyhouse.core.entity.review.Review;
import com.tastyhouse.core.entity.review.dto.MyReviewListItemDto;
import com.tastyhouse.core.entity.review.ReviewComment;
import com.tastyhouse.core.entity.review.ReviewImage;
import com.tastyhouse.core.entity.review.ReviewReply;
import com.tastyhouse.core.entity.review.ReviewTag;
import com.tastyhouse.core.entity.review.dto.BestReviewListItemDto;
import com.tastyhouse.core.entity.review.dto.LatestReviewListItemDto;
import com.tastyhouse.core.entity.review.dto.PlaceReviewStatisticsDto;
import com.tastyhouse.core.entity.review.dto.ReviewDetailDto;
import com.tastyhouse.core.repository.follow.FollowJpaRepository;
import com.tastyhouse.core.repository.place.TagJpaRepository;
import com.tastyhouse.core.repository.review.ReviewImageJpaRepository;
import com.tastyhouse.core.repository.review.ReviewJpaRepository;
import com.tastyhouse.core.repository.review.ReviewRepository;
import com.tastyhouse.core.repository.review.ReviewLikeJpaRepository;
import com.tastyhouse.core.repository.review.ReviewTagJpaRepository;
import com.tastyhouse.core.repository.review.ReviewCommentJpaRepository;
import com.tastyhouse.core.repository.review.ReviewReplyJpaRepository;
import com.tastyhouse.core.entity.review.ReviewLike;
import com.tastyhouse.core.exception.EntityNotFoundException;
import com.tastyhouse.core.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewCoreService {

    private final ReviewRepository reviewRepository;
    private final ReviewJpaRepository reviewJpaRepository;
    private final FollowJpaRepository followJpaRepository;
    private final ReviewTagJpaRepository reviewTagJpaRepository;
    private final TagJpaRepository tagJpaRepository;
    private final ReviewLikeJpaRepository reviewLikeJpaRepository;
    private final ReviewCommentJpaRepository reviewCommentJpaRepository;
    private final ReviewReplyJpaRepository reviewReplyJpaRepository;
    private final ReviewImageJpaRepository reviewImageJpaRepository;

    @Transactional(readOnly = true)
    public PageResult<BestReviewListItemDto> findBestReviewsWithPagination(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<BestReviewListItemDto> reviewPage = reviewRepository.findBestReviews(pageRequest);
        return PageResult.from(reviewPage);
    }

    @Transactional(readOnly = true)
    public Review findById(Long id) {
        return reviewJpaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Optional<ReviewDetailDto> findReviewDetail(Long reviewId) {
        return reviewRepository.findReviewDetail(reviewId).map(dto -> {
            List<Long> tagIds = reviewTagJpaRepository.findTagIdsByReviewId(reviewId);
            if (!tagIds.isEmpty()) {
                List<String> tagNames = tagJpaRepository.findTagNamesByIds(tagIds);
                return dto.withTagNames(tagNames);
            }
            return dto;
        });
    }

    @Transactional(readOnly = true)
    public boolean isLikedByMember(Long reviewId, Long memberId) {
        return reviewLikeJpaRepository.existsByReviewIdAndMemberId(reviewId, memberId);
    }

    @Transactional(readOnly = true)
    public PageResult<LatestReviewListItemDto> findLatestReviewsWithPagination(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<LatestReviewListItemDto> reviewPage = reviewRepository.findLatestReviews(pageRequest);
        return PageResult.from(reviewPage);
    }

    @Transactional(readOnly = true)
    public PageResult<LatestReviewListItemDto> findLatestReviewsByFollowingWithPagination(Long memberId, int page, int size) {
        List<Long> followingMemberIds = followJpaRepository.findFollowingIdsByFollowerId(memberId);

        if (followingMemberIds.isEmpty()) {
            return new PageResult<>(
                List.of(),
                0L,
                0,
                page,
                size
            );
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<LatestReviewListItemDto> reviewPage = reviewRepository.findLatestReviewsByFollowing(
            followingMemberIds,
            pageRequest
        );

        return new PageResult<>(
            reviewPage.getContent(),
            reviewPage.getTotalElements(),
            reviewPage.getTotalPages(),
            reviewPage.getNumber(),
            reviewPage.getSize()
        );
    }

    @Transactional(readOnly = true)
    public ReviewsByRatingResult findPlaceReviewsByRating(Long placeId, int page, int size) {
        List<LatestReviewListItemDto> rating1Reviews = reviewRepository.findReviewsByPlaceIdAndRating(placeId, 1, 5);
        List<LatestReviewListItemDto> rating2Reviews = reviewRepository.findReviewsByPlaceIdAndRating(placeId, 2, 5);
        List<LatestReviewListItemDto> rating3Reviews = reviewRepository.findReviewsByPlaceIdAndRating(placeId, 3, 5);
        List<LatestReviewListItemDto> rating4Reviews = reviewRepository.findReviewsByPlaceIdAndRating(placeId, 4, 5);
        List<LatestReviewListItemDto> rating5Reviews = reviewRepository.findReviewsByPlaceIdAndRating(placeId, 5, 5);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<LatestReviewListItemDto> allReviewsPage = reviewRepository.findLatestReviewsByPlaceId(placeId, null, pageRequest, null, "LATEST");
        List<LatestReviewListItemDto> allReviews = allReviewsPage.getContent();

        Long totalReviewCount = reviewJpaRepository.countByPlaceIdAndIsHiddenFalse(placeId);

        Map<Integer, List<LatestReviewListItemDto>> reviewsByRating = new HashMap<>();
        reviewsByRating.put(1, rating1Reviews);
        reviewsByRating.put(2, rating2Reviews);
        reviewsByRating.put(3, rating3Reviews);
        reviewsByRating.put(4, rating4Reviews);
        reviewsByRating.put(5, rating5Reviews);

        return new ReviewsByRatingResult(
            reviewsByRating,
            allReviews,
            totalReviewCount,
            allReviewsPage.getTotalElements(),
            allReviewsPage.getTotalPages(),
            allReviewsPage.getNumber(),
            allReviewsPage.getSize()
        );
    }

    @Transactional
    public boolean toggleReviewLike(Long reviewId, Long memberId) {
        boolean alreadyLiked = reviewLikeJpaRepository.existsByReviewIdAndMemberId(reviewId, memberId);

        if (alreadyLiked) {
            reviewLikeJpaRepository.deleteByReviewIdAndMemberId(reviewId, memberId);
            return false;
        } else {
            ReviewLike reviewLike = new ReviewLike(reviewId, memberId);
            reviewLikeJpaRepository.save(reviewLike);
            return true;
        }
    }

    @Transactional
    public ReviewComment createComment(Long reviewId, Long memberId, String content) {
        ReviewComment comment = new ReviewComment(reviewId, memberId, content);
        return reviewCommentJpaRepository.save(comment);
    }

    @Transactional
    public ReviewReply createReply(Long commentId, Long memberId, Long replyToMemberId, String content) {
        ReviewReply reply = new ReviewReply(commentId, memberId, replyToMemberId, content);
        return reviewReplyJpaRepository.save(reply);
    }

    @Transactional(readOnly = true)
    public List<ReviewComment> findCommentsByReviewId(Long reviewId) {
        return reviewCommentJpaRepository.findByReviewIdAndIsHiddenFalseOrderByCreatedAtDesc(reviewId);
    }

    @Transactional(readOnly = true)
    public List<ReviewReply> findRepliesByCommentIds(List<Long> commentIds) {
        if (commentIds.isEmpty()) {
            return List.of();
        }
        return reviewReplyJpaRepository.findByCommentIdInAndIsHiddenFalseOrderByCreatedAtAsc(commentIds);
    }

    @Transactional(readOnly = true)
    public PageResult<Review> findPlaceReviews(Long placeId, Integer rating, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Review> reviewPage;

        if (rating != null) {
            reviewPage = reviewJpaRepository.findByPlaceIdAndTotalRatingAndIsHiddenFalseOrderByCreatedAtDesc(
                    placeId, rating.doubleValue(), pageRequest
            );
        } else {
            reviewPage = reviewJpaRepository.findByPlaceIdAndIsHiddenFalseOrderByCreatedAtDesc(placeId, pageRequest);
        }

        return PageResult.from(reviewPage);
    }

    @Transactional(readOnly = true)
    public List<ReviewImage> findReviewImages(List<Long> reviewIds) {
        return reviewImageJpaRepository.findByReviewIdInOrderBySortAsc(reviewIds);
    }

    @Transactional(readOnly = true)
    public PlaceReviewStatisticsDto findPlaceReviewStatistics(Long placeId) {
        Long totalCount = reviewJpaRepository.countByPlaceIdAndIsHiddenFalse(placeId);

        Object[][] ratingData = reviewJpaRepository.getRatingCounts(placeId);
        Map<Integer, Long> ratingMap = new HashMap<>();
        for (Object[] row : ratingData) {
            ratingMap.put(((Number) row[0]).intValue(), (Long) row[1]);
        }
        for (int r = 1; r <= 5; r++) {
            ratingMap.putIfAbsent(r, 0L);
        }

        if (totalCount > 0) {
            Long willRevisitCount = reviewJpaRepository.countWillRevisit(placeId);
            double willRevisitPercentage = (willRevisitCount * 100.0) / totalCount;

            int currentYear = LocalDateTime.now().getYear();
            Object[][] monthlyData = reviewJpaRepository.getMonthlyReviewCounts(placeId, currentYear);
            Map<Integer, Long> monthlyMap = new HashMap<>();
            for (Object[] row : monthlyData) {
                monthlyMap.put((Integer) row[0], (Long) row[1]);
            }

            return new PlaceReviewStatisticsDto(
                    totalCount,
                    reviewJpaRepository.getAverageTasteRating(placeId),
                    reviewJpaRepository.getAverageAmountRating(placeId),
                    reviewJpaRepository.getAveragePriceRating(placeId),
                    reviewJpaRepository.getAverageAtmosphereRating(placeId),
                    reviewJpaRepository.getAverageKindnessRating(placeId),
                    reviewJpaRepository.getAverageHygieneRating(placeId),
                    willRevisitPercentage,
                    ratingMap,
                    monthlyMap
            );
        }

        return new PlaceReviewStatisticsDto(
                totalCount,
                null, null, null, null, null, null, null,
                ratingMap,
                null
        );
    }

    @Transactional(readOnly = true)
    public ReviewsByRatingResult findProductReviewsByRating(Long productId, int page, int size) {
        List<LatestReviewListItemDto> rating1Reviews = reviewRepository.findReviewsByProductIdAndRating(productId, 1, 5);
        List<LatestReviewListItemDto> rating2Reviews = reviewRepository.findReviewsByProductIdAndRating(productId, 2, 5);
        List<LatestReviewListItemDto> rating3Reviews = reviewRepository.findReviewsByProductIdAndRating(productId, 3, 5);
        List<LatestReviewListItemDto> rating4Reviews = reviewRepository.findReviewsByProductIdAndRating(productId, 4, 5);
        List<LatestReviewListItemDto> rating5Reviews = reviewRepository.findReviewsByProductIdAndRating(productId, 5, 5);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<LatestReviewListItemDto> allReviewsPage = reviewRepository.findLatestReviewsByProductId(productId, null, pageRequest, null, "LATEST");
        List<LatestReviewListItemDto> allReviews = allReviewsPage.getContent();

        Long totalReviewCount = reviewJpaRepository.countByProductIdAndIsHiddenFalse(productId);

        Map<Integer, List<LatestReviewListItemDto>> reviewsByRating = new HashMap<>();
        reviewsByRating.put(1, rating1Reviews);
        reviewsByRating.put(2, rating2Reviews);
        reviewsByRating.put(3, rating3Reviews);
        reviewsByRating.put(4, rating4Reviews);
        reviewsByRating.put(5, rating5Reviews);

        return new ReviewsByRatingResult(
            reviewsByRating,
            allReviews,
            totalReviewCount,
            allReviewsPage.getTotalElements(),
            allReviewsPage.getTotalPages(),
            allReviewsPage.getNumber(),
            allReviewsPage.getSize()
        );
    }

    @Transactional(readOnly = true)
    public PageResult<MyReviewListItemDto> findReviewsByMemberId(Long memberId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<MyReviewListItemDto> reviewPage = reviewRepository.findReviewsByMemberId(memberId, pageRequest);
        return PageResult.from(reviewPage);
    }

    @Transactional(readOnly = true)
    public boolean isReviewedByOrderAndProduct(Long orderId, Long productId, Long memberId) {
        return reviewJpaRepository.existsByOrderIdAndProductIdAndMemberId(orderId, productId, memberId);
    }

    @Transactional
    public Review saveReview(Review review) {
        return reviewJpaRepository.save(review);
    }

    @Transactional
    public void saveReviewImages(List<ReviewImage> images) {
        reviewImageJpaRepository.saveAll(images);
    }

    @Transactional
    public void saveReviewTags(List<ReviewTag> tags) {
        reviewTagJpaRepository.saveAll(tags);
    }

    @Transactional(readOnly = true)
    public Optional<Review> findReviewByIdAndMemberId(Long reviewId, Long memberId) {
        return reviewJpaRepository.findByIdAndMemberId(reviewId, memberId);
    }

    @Transactional
    public void deleteReviewImages(Long reviewId) {
        reviewImageJpaRepository.deleteByReviewId(reviewId);
    }

    @Transactional
    public void deleteReviewTags(Long reviewId) {
        reviewTagJpaRepository.deleteByReviewId(reviewId);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        reviewJpaRepository.deleteById(reviewId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findProductReviewStatistics(Long productId) {
        Map<String, Object> statistics = new HashMap<>();

        Long totalCount = reviewJpaRepository.countByProductIdAndIsHiddenFalse(productId);
        statistics.put("totalReviewCount", totalCount);

        if (totalCount > 0) {
            statistics.put("averageTasteRating", reviewJpaRepository.getAverageTasteRatingByProductId(productId));
            statistics.put("averageAmountRating", reviewJpaRepository.getAverageAmountRatingByProductId(productId));
            statistics.put("averagePriceRating", reviewJpaRepository.getAveragePriceRatingByProductId(productId));
        }

        return statistics;
    }
}
