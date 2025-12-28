package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.review.Review;
import com.tastyhouse.core.entity.review.dto.BestReviewListItemDto;
import com.tastyhouse.core.entity.review.dto.LatestReviewListItemDto;
import com.tastyhouse.core.entity.review.dto.ReviewDetailDto;
import com.tastyhouse.core.repository.follow.FollowJpaRepository;
import com.tastyhouse.core.repository.place.TagJpaRepository;
import com.tastyhouse.core.repository.review.ReviewJpaRepository;
import com.tastyhouse.core.repository.review.ReviewRepository;
import com.tastyhouse.core.repository.review.ReviewLikeJpaRepository;
import com.tastyhouse.core.repository.review.ReviewTagJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewCoreService {

    private final ReviewRepository reviewRepository;
    private final ReviewJpaRepository reviewJpaRepository;
    private final FollowJpaRepository followJpaRepository;
    private final ReviewTagJpaRepository reviewTagJpaRepository;
    private final TagJpaRepository tagJpaRepository;
    private final ReviewLikeJpaRepository reviewLikeJpaRepository;

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

    public Optional<ReviewDetailDto> findReviewDetail(Long reviewId, Long memberId) {
        Optional<ReviewDetailDto> result = reviewRepository.findReviewDetail(reviewId);

        result.ifPresent(dto -> {
            List<Long> tagIds = reviewTagJpaRepository.findTagIdsByReviewId(reviewId);
            if (!tagIds.isEmpty()) {
                List<String> tagNames = tagJpaRepository.findTagNamesByIds(tagIds);
                dto.setTagNames(tagNames);
            }

            if (memberId != null) {
                boolean isLiked = reviewLikeJpaRepository.existsByReviewIdAndMemberId(reviewId, memberId);
                dto.setIsLiked(isLiked);
            } else {
                dto.setIsLiked(false);
            }
        });

        return result;
    }

    public LatestReviewPageResult findLatestReviewsWithPagination(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<LatestReviewListItemDto> reviewPage = reviewRepository.findLatestReviews(pageRequest);

        return new LatestReviewPageResult(
            reviewPage.getContent(),
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

        return new LatestReviewPageResult(
            reviewPage.getContent(),
            reviewPage.getTotalElements(),
            reviewPage.getTotalPages(),
            reviewPage.getNumber(),
            reviewPage.getSize()
        );
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