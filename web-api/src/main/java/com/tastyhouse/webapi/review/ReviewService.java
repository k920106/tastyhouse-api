package com.tastyhouse.webapi.review;

import com.tastyhouse.core.entity.review.dto.BestReviewListItemDto;
import com.tastyhouse.core.entity.review.dto.LatestReviewListItemDto;
import com.tastyhouse.core.service.ReviewCoreService;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.review.request.ReviewType;
import com.tastyhouse.webapi.review.response.BestReviewListItem;
import com.tastyhouse.webapi.review.response.LatestReviewListItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewCoreService reviewCoreService;

    public PageResult<BestReviewListItem> findBestReviewList(PageRequest pageRequest) {
        ReviewCoreService.ReviewPageResult coreResult = reviewCoreService.findBestReviewsWithPagination(
            pageRequest.getPage(), pageRequest.getSize()
        );

        List<BestReviewListItem> reviewListItems = coreResult.getContent().stream()
            .map(this::convertToBestReviewListItem)
            .toList();

        return new PageResult<>(
            reviewListItems,
            coreResult.getTotalElements(),
            coreResult.getTotalPages(),
            coreResult.getCurrentPage(),
            coreResult.getPageSize()
        );
    }

    private BestReviewListItem convertToBestReviewListItem(BestReviewListItemDto dto) {
        return BestReviewListItem.builder()
            .id(dto.getId())
            .imageUrl(dto.getImageUrl())
            .stationName(dto.getStationName())
            .totalRating(dto.getTotalRating())
            .title(dto.getTitle())
            .content(dto.getContent())
            .build();
    }

    public PageResult<LatestReviewListItem> findLatestReviewList(
        PageRequest pageRequest,
        ReviewType type,
        Long memberId
    ) {
        ReviewCoreService.LatestReviewPageResult coreResult;

        if (type == ReviewType.FOLLOWING && memberId != null) {
            coreResult = reviewCoreService.findLatestReviewsByFollowingWithPagination(
                memberId,
                pageRequest.getPage(),
                pageRequest.getSize()
            );
        } else {
            coreResult = reviewCoreService.findLatestReviewsWithPagination(
                pageRequest.getPage(),
                pageRequest.getSize()
            );
        }

        List<LatestReviewListItem> reviewListItems = coreResult.getContent().stream()
            .map(this::convertToLatestReviewListItem)
            .toList();

        return new PageResult<>(
            reviewListItems,
            coreResult.getTotalElements(),
            coreResult.getTotalPages(),
            coreResult.getCurrentPage(),
            coreResult.getPageSize()
        );
    }

    private LatestReviewListItem convertToLatestReviewListItem(LatestReviewListItemDto dto) {
        return LatestReviewListItem.builder()
            .id(dto.getId())
            .imageUrls(dto.getImageUrls())
            .stationName(dto.getStationName())
            .totalRating(dto.getTotalRating())
            .title(dto.getTitle())
            .content(dto.getContent())
            .memberId(dto.getMemberId())
            .memberNickname(dto.getMemberNickname())
            .memberProfileImageUrl(dto.getMemberProfileImageUrl())
            .createdAt(dto.getCreatedAt())
            .build();
    }
}
