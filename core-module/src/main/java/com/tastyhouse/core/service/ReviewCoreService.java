package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.review.Review;
import com.tastyhouse.core.entity.review.ReviewComment;
import com.tastyhouse.core.entity.review.ReviewImage;
import com.tastyhouse.core.entity.review.ReviewReply;
import com.tastyhouse.core.entity.review.dto.BestReviewListItemDto;
import com.tastyhouse.core.entity.review.dto.LatestReviewListItemDto;
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

    public ReviewPageResult findBestReviewsWithPagination(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<BestReviewListItemDto> reviewPage = reviewRepository.findBestReviews(
            pageRequest
        );

        return new ReviewPageResult(
            reviewPage.getContent(),
            reviewPage.getTotalElements(),
            reviewPage.getTotalPages(),
            reviewPage.getNumber(),
            reviewPage.getSize()
        );
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

    public LatestReviewPageResult findLatestReviewsWithPagination(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<LatestReviewListItemDto> reviewPage = reviewRepository.findLatestReviews(pageRequest);

        List<LatestReviewListItemDto> content = reviewPage.getContent();
        if (!content.isEmpty()) {
            populateLikeAndCommentCounts(content);
        }

        return new LatestReviewPageResult(
            content,
            reviewPage.getTotalElements(),
            reviewPage.getTotalPages(),
            reviewPage.getNumber(),
            reviewPage.getSize()
        );
    }

    public LatestReviewPageResult findLatestReviewsByFollowingWithPagination(Long memberId, int page, int size) {
        List<Long> followingMemberIds = followJpaRepository.findFollowingIdsByFollowerId(memberId);

        if (followingMemberIds.isEmpty()) {
            return new LatestReviewPageResult(
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

        return new LatestReviewPageResult(
            content,
            reviewPage.getTotalElements(),
            reviewPage.getTotalPages(),
            reviewPage.getNumber(),
            reviewPage.getSize()
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

    public PlaceReviewPageResult findPlaceReviews(Long placeId, Integer rating, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Review> reviewPage;

        if (rating != null) {
            reviewPage = reviewJpaRepository.findByPlaceIdAndTotalRatingAndIsHiddenFalseOrderByCreatedAtDesc(
                    placeId, rating.doubleValue(), pageRequest
            );
        } else {
            reviewPage = reviewJpaRepository.findByPlaceIdAndIsHiddenFalseOrderByCreatedAtDesc(placeId, pageRequest);
        }

        return new PlaceReviewPageResult(
                reviewPage.getContent(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.getNumber(),
                reviewPage.getSize()
        );
    }

    public List<ReviewImage> findReviewImages(List<Long> reviewIds) {
        return reviewImageJpaRepository.findByReviewIdInOrderBySortAsc(reviewIds);
    }

    public Map<String, Object> getPlaceReviewStatistics(Long placeId) {
        Map<String, Object> statistics = new HashMap<>();

        Long totalCount = reviewJpaRepository.countByPlaceIdAndIsHiddenFalse(placeId);
        statistics.put("totalReviewCount", totalCount);

        if (totalCount > 0) {
            statistics.put("averageTasteRating", reviewJpaRepository.getAverageTasteRating(placeId));
            statistics.put("averageAmountRating", reviewJpaRepository.getAverageAmountRating(placeId));
            statistics.put("averagePriceRating", reviewJpaRepository.getAveragePriceRating(placeId));
            statistics.put("averageAtmosphereRating", reviewJpaRepository.getAverageAtmosphereRating(placeId));
            statistics.put("averageKindnessRating", reviewJpaRepository.getAverageKindnessRating(placeId));
            statistics.put("averageHygieneRating", reviewJpaRepository.getAverageHygieneRating(placeId));

            Long willRevisitCount = reviewJpaRepository.countWillRevisit(placeId);
            double willRevisitPercentage = (willRevisitCount * 100.0) / totalCount;
            statistics.put("willRevisitPercentage", willRevisitPercentage);

            int currentYear = LocalDateTime.now().getYear();
            Object[][] monthlyData = reviewJpaRepository.getMonthlyReviewCounts(placeId, currentYear);
            Map<Integer, Long> monthlyMap = new HashMap<>();
            for (Object[] row : monthlyData) {
                monthlyMap.put((Integer) row[0], (Long) row[1]);
            }
            statistics.put("monthlyReviewCounts", monthlyMap);

            Object[][] ratingData = reviewJpaRepository.getRatingCounts(placeId);
            Map<Integer, Long> ratingMap = new HashMap<>();
            for (Object[] row : ratingData) {
                ratingMap.put(((Number) row[0]).intValue(), (Long) row[1]);
            }
            statistics.put("ratingCounts", ratingMap);
        }

        return statistics;
    }

    public static class PlaceReviewPageResult {
        private final List<Review> content;
        private final long totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;

        public PlaceReviewPageResult(List<Review> content, long totalElements, int totalPages, int currentPage, int pageSize) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }

        public List<Review> getContent() { return content; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
    }

    public static class LatestReviewPageResult {
        private final List<LatestReviewListItemDto> content;
        private final long totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;

        public LatestReviewPageResult(List<LatestReviewListItemDto> content, long totalElements, int totalPages, int currentPage, int pageSize) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }

        public List<LatestReviewListItemDto> getContent() { return content; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
    }

    public static class ReviewPageResult {
        private final List<BestReviewListItemDto> content;
        private final long totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;

        public ReviewPageResult(List<BestReviewListItemDto> content, long totalElements, int totalPages, int currentPage, int pageSize) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }

        public List<BestReviewListItemDto> getContent() { return content; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
    }
}
