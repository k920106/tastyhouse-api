package com.tastyhouse.core.service;

import com.tastyhouse.core.common.PageResult;
import com.tastyhouse.core.common.ReviewsByRatingResult;
import com.tastyhouse.core.entity.review.Review;
import com.tastyhouse.core.entity.review.ReviewComment;
import com.tastyhouse.core.entity.review.ReviewImage;
import com.tastyhouse.core.entity.review.ReviewReply;
import com.tastyhouse.core.entity.review.dto.BestReviewListItemDto;
import com.tastyhouse.core.entity.review.dto.LatestReviewListItemDto;
import com.tastyhouse.core.entity.review.dto.PlaceReviewStatisticsDto;
import com.tastyhouse.core.entity.review.dto.ReviewDetailDto;
import com.tastyhouse.core.entity.user.Member;
import com.tastyhouse.core.repository.follow.FollowJpaRepository;
import com.tastyhouse.core.repository.member.MemberJpaRepository;
import com.tastyhouse.core.repository.place.TagJpaRepository;
import com.tastyhouse.core.repository.review.ReviewImageJpaRepository;
import com.tastyhouse.core.repository.review.ReviewJpaRepository;
import com.tastyhouse.core.repository.review.ReviewRepository;
import com.tastyhouse.core.repository.review.ReviewLikeJpaRepository;
import com.tastyhouse.core.repository.review.ReviewTagJpaRepository;
import com.tastyhouse.core.repository.review.ReviewCommentJpaRepository;
import com.tastyhouse.core.repository.review.ReviewReplyJpaRepository;
import com.tastyhouse.core.entity.review.ReviewLike;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private final MemberJpaRepository memberJpaRepository;

    public PageResult<BestReviewListItemDto> findBestReviewsWithPagination(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<BestReviewListItemDto> reviewPage = reviewRepository.findBestReviews(pageRequest);
        return PageResult.from(reviewPage);
    }

    public Review findById(Long id) {
        return reviewJpaRepository.findById(id).orElse(null);
    }

    public Optional<ReviewDetailDto> findReviewDetail(Long reviewId) {
        Optional<ReviewDetailDto> result = reviewRepository.findReviewDetail(reviewId);

        result.ifPresent(dto -> {
            List<Long> tagIds = reviewTagJpaRepository.findTagIdsByReviewId(reviewId);
            if (!tagIds.isEmpty()) {
                List<String> tagNames = tagJpaRepository.findTagNamesByIds(tagIds);
                dto.setTagNames(tagNames);
            }
        });

        return result;
    }

    public boolean isLikedByMember(Long reviewId, Long memberId) {
        return reviewLikeJpaRepository.existsByReviewIdAndMemberId(reviewId, memberId);
    }

    public PageResult<LatestReviewListItemDto> findLatestReviewsWithPagination(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<LatestReviewListItemDto> reviewPage = reviewRepository.findLatestReviews(pageRequest);

        List<LatestReviewListItemDto> content = reviewPage.getContent();
        if (!content.isEmpty()) {
            populateLikeAndCommentCounts(content);
        }

        return new PageResult<>(
            content,
            reviewPage.getTotalElements(),
            reviewPage.getTotalPages(),
            reviewPage.getNumber(),
            reviewPage.getSize()
        );
    }

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

        List<LatestReviewListItemDto> content = reviewPage.getContent();
        if (!content.isEmpty()) {
            populateLikeAndCommentCounts(content);
        }

        return new PageResult<>(
            content,
            reviewPage.getTotalElements(),
            reviewPage.getTotalPages(),
            reviewPage.getNumber(),
            reviewPage.getSize()
        );
    }

    public ReviewsByRatingResult getPlaceReviewsByRating(Long placeId, int page, int size) {
        List<LatestReviewListItemDto> rating1Reviews = reviewRepository.findReviewsByPlaceIdAndRating(placeId, 1, 5);
        List<LatestReviewListItemDto> rating2Reviews = reviewRepository.findReviewsByPlaceIdAndRating(placeId, 2, 5);
        List<LatestReviewListItemDto> rating3Reviews = reviewRepository.findReviewsByPlaceIdAndRating(placeId, 3, 5);
        List<LatestReviewListItemDto> rating4Reviews = reviewRepository.findReviewsByPlaceIdAndRating(placeId, 4, 5);
        List<LatestReviewListItemDto> rating5Reviews = reviewRepository.findReviewsByPlaceIdAndRating(placeId, 5, 5);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<LatestReviewListItemDto> allReviewsPage = reviewRepository.findLatestReviewsByPlaceId(placeId, null, pageRequest, null, "LATEST");
        List<LatestReviewListItemDto> allReviews = allReviewsPage.getContent();

        Long totalReviewCount = reviewJpaRepository.countByPlaceIdAndIsHiddenFalse(placeId);

        List<LatestReviewListItemDto> allReviewsForPopulate = new ArrayList<>();
        allReviewsForPopulate.addAll(rating1Reviews);
        allReviewsForPopulate.addAll(rating2Reviews);
        allReviewsForPopulate.addAll(rating3Reviews);
        allReviewsForPopulate.addAll(rating4Reviews);
        allReviewsForPopulate.addAll(rating5Reviews);
        allReviewsForPopulate.addAll(allReviews);

        if (!allReviewsForPopulate.isEmpty()) {
            populateLikeAndCommentCounts(allReviewsForPopulate);
        }

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

    private void populateLikeAndCommentCounts(List<LatestReviewListItemDto> reviews) {
        List<Long> reviewIds = reviews.stream()
            .map(LatestReviewListItemDto::getId)
            .toList();

        Map<Long, Long> likeCountMap = reviewLikeJpaRepository.countByReviewIdIn(reviewIds).stream()
            .collect(Collectors.toMap(
                arr -> (Long) arr[0],
                arr -> (Long) arr[1]
            ));

        Map<Long, Long> commentCountMap = reviewCommentJpaRepository.countByReviewIdIn(reviewIds).stream()
            .collect(Collectors.toMap(
                arr -> (Long) arr[0],
                arr -> (Long) arr[1]
            ));

        reviews.forEach(review -> {
            review.setLikeCount(likeCountMap.getOrDefault(review.getId(), 0L));
            review.setCommentCount(commentCountMap.getOrDefault(review.getId(), 0L));
        });
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

    public List<ReviewComment> findCommentsByReviewId(Long reviewId) {
        return reviewCommentJpaRepository.findByReviewIdAndIsHiddenFalseOrderByCreatedAtDesc(reviewId);
    }

    public List<ReviewReply> findRepliesByCommentIds(List<Long> commentIds) {
        if (commentIds.isEmpty()) {
            return List.of();
        }
        return reviewReplyJpaRepository.findByCommentIdInAndIsHiddenFalseOrderByCreatedAtAsc(commentIds);
    }

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

    public List<ReviewImage> findReviewImages(List<Long> reviewIds) {
        return reviewImageJpaRepository.findByReviewIdInOrderBySortAsc(reviewIds);
    }

    public PlaceReviewStatisticsDto getPlaceReviewStatistics(Long placeId) {
        Long totalCount = reviewJpaRepository.countByPlaceIdAndIsHiddenFalse(placeId);

        Object[][] ratingData = reviewJpaRepository.getRatingCounts(placeId);
        Map<Integer, Long> ratingMap = new HashMap<>();
        for (Object[] row : ratingData) {
            ratingMap.put(((Number) row[0]).intValue(), (Long) row[1]);
        }
        for (int r = 1; r <= 5; r++) {
            ratingMap.putIfAbsent(r, 0L);
        }

        PlaceReviewStatisticsDto.PlaceReviewStatisticsDtoBuilder builder = PlaceReviewStatisticsDto.builder()
                .totalReviewCount(totalCount)
                .ratingCounts(ratingMap);

        if (totalCount > 0) {
            Long willRevisitCount = reviewJpaRepository.countWillRevisit(placeId);
            double willRevisitPercentage = (willRevisitCount * 100.0) / totalCount;

            int currentYear = LocalDateTime.now().getYear();
            Object[][] monthlyData = reviewJpaRepository.getMonthlyReviewCounts(placeId, currentYear);
            Map<Integer, Long> monthlyMap = new HashMap<>();
            for (Object[] row : monthlyData) {
                monthlyMap.put((Integer) row[0], (Long) row[1]);
            }

            builder.averageTasteRating(reviewJpaRepository.getAverageTasteRating(placeId))
                    .averageAmountRating(reviewJpaRepository.getAverageAmountRating(placeId))
                    .averagePriceRating(reviewJpaRepository.getAveragePriceRating(placeId))
                    .averageAtmosphereRating(reviewJpaRepository.getAverageAtmosphereRating(placeId))
                    .averageKindnessRating(reviewJpaRepository.getAverageKindnessRating(placeId))
                    .averageHygieneRating(reviewJpaRepository.getAverageHygieneRating(placeId))
                    .willRevisitPercentage(willRevisitPercentage)
                    .monthlyReviewCounts(monthlyMap);
        }

        return builder.build();
    }

    public ReviewsByRatingResult getProductReviewsByRating(Long productId, int page, int size) {
        List<LatestReviewListItemDto> rating1Reviews = reviewRepository.findReviewsByProductIdAndRating(productId, 1, 5);
        List<LatestReviewListItemDto> rating2Reviews = reviewRepository.findReviewsByProductIdAndRating(productId, 2, 5);
        List<LatestReviewListItemDto> rating3Reviews = reviewRepository.findReviewsByProductIdAndRating(productId, 3, 5);
        List<LatestReviewListItemDto> rating4Reviews = reviewRepository.findReviewsByProductIdAndRating(productId, 4, 5);
        List<LatestReviewListItemDto> rating5Reviews = reviewRepository.findReviewsByProductIdAndRating(productId, 5, 5);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<LatestReviewListItemDto> allReviewsPage = reviewRepository.findLatestReviewsByProductId(productId, null, pageRequest, null, "LATEST");
        List<LatestReviewListItemDto> allReviews = allReviewsPage.getContent();

        Long totalReviewCount = reviewJpaRepository.countByProductIdAndIsHiddenFalse(productId);

        List<LatestReviewListItemDto> allReviewsForPopulate = new ArrayList<>();
        allReviewsForPopulate.addAll(rating1Reviews);
        allReviewsForPopulate.addAll(rating2Reviews);
        allReviewsForPopulate.addAll(rating3Reviews);
        allReviewsForPopulate.addAll(rating4Reviews);
        allReviewsForPopulate.addAll(rating5Reviews);
        allReviewsForPopulate.addAll(allReviews);

        if (!allReviewsForPopulate.isEmpty()) {
            populateLikeAndCommentCounts(allReviewsForPopulate);
        }

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

    public Map<String, Object> getProductReviewStatistics(Long productId) {
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
