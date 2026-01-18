package com.tastyhouse.webapi.review;

import com.tastyhouse.core.entity.review.Review;
import com.tastyhouse.core.entity.review.ReviewComment;
import com.tastyhouse.core.entity.review.ReviewReply;
import com.tastyhouse.core.entity.review.dto.BestReviewListItemDto;
import com.tastyhouse.core.entity.review.dto.LatestReviewListItemDto;
import com.tastyhouse.core.entity.review.dto.ReviewDetailDto;
import com.tastyhouse.core.entity.user.Member;
import com.tastyhouse.core.repository.member.MemberJpaRepository;
import com.tastyhouse.core.service.ProductCoreService;
import com.tastyhouse.core.service.ReviewCoreService;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.review.request.ReviewType;
import com.tastyhouse.webapi.review.response.BestReviewListItem;
import com.tastyhouse.webapi.review.response.CommentListResponse;
import com.tastyhouse.webapi.review.response.CommentResponse;
import com.tastyhouse.webapi.review.response.LatestReviewListItem;
import com.tastyhouse.webapi.review.response.ReplyResponse;
import com.tastyhouse.webapi.review.response.ReviewDetailResponse;
import com.tastyhouse.webapi.review.response.ReviewLikeStatusResponse;
import com.tastyhouse.webapi.review.response.ReviewProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewCoreService reviewCoreService;
    private final ProductCoreService productCoreService;
    private final MemberJpaRepository memberJpaRepository;

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
            .content(dto.getContent())
            .memberId(dto.getMemberId())
            .memberNickname(dto.getMemberNickname())
            .memberProfileImageUrl(dto.getMemberProfileImageUrl())
            .createdAt(dto.getCreatedAt())
            .likeCount(dto.getLikeCount())
            .commentCount(dto.getCommentCount())
            .build();
    }

    public Optional<ReviewDetailResponse> findReviewDetail(Long reviewId) {
        return reviewCoreService.findReviewDetail(reviewId)
            .map(this::convertToReviewDetailResponse);
    }

    private ReviewDetailResponse convertToReviewDetailResponse(ReviewDetailDto dto) {
        return ReviewDetailResponse.builder()
            .id(dto.getId())
            .placeId(dto.getPlaceId())
            .placeName(dto.getPlaceName())
            .stationName(dto.getStationName())
            .content(dto.getContent())
            .totalRating(dto.getTotalRating())
            .tasteRating(dto.getTasteRating())
            .amountRating(dto.getAmountRating())
            .priceRating(dto.getPriceRating())
            .atmosphereRating(dto.getAtmosphereRating())
            .kindnessRating(dto.getKindnessRating())
            .hygieneRating(dto.getHygieneRating())
            .willRevisit(dto.getWillRevisit())
            .memberId(dto.getMemberId())
            .memberNickname(dto.getMemberNickname())
            .memberProfileImageUrl(dto.getMemberProfileImageUrl())
            .createdAt(dto.getCreatedAt())
            .imageUrls(dto.getImageUrls())
            .tagNames(dto.getTagNames())
            .build();
    }

    public ReviewLikeStatusResponse isLiked(Long reviewId, Long memberId) {
        boolean isLiked = reviewCoreService.isLikedByMember(reviewId, memberId);
        return new ReviewLikeStatusResponse(isLiked);
    }

    public boolean toggleReviewLike(Long reviewId, Long memberId) {
        return reviewCoreService.toggleReviewLike(reviewId, memberId);
    }

    public CommentResponse createComment(Long reviewId, Long memberId, String content) {
        ReviewComment comment = reviewCoreService.createComment(reviewId, memberId, content);
        Member member = memberJpaRepository.findById(memberId).orElse(null);
        return convertToCommentResponse(comment, member, List.of());
    }

    public ReplyResponse createReply(Long commentId, Long memberId, Long replyToMemberId, String content) {
        ReviewReply reply = reviewCoreService.createReply(commentId, memberId, replyToMemberId, content);
        Member member = memberJpaRepository.findById(memberId).orElse(null);
        Member replyToMember = replyToMemberId != null ? memberJpaRepository.findById(replyToMemberId).orElse(null) : null;
        return convertToReplyResponse(reply, member, replyToMember);
    }

    public CommentListResponse findCommentsWithReplies(Long reviewId) {
        List<ReviewComment> comments = reviewCoreService.findCommentsByReviewId(reviewId);

        if (comments.isEmpty()) {
            return new CommentListResponse(List.of(), 0);
        }

        List<Long> commentIds = comments.stream()
            .map(ReviewComment::getId)
            .toList();

        List<ReviewReply> allReplies = reviewCoreService.findRepliesByCommentIds(commentIds);

        Map<Long, List<ReviewReply>> repliesByCommentId = allReplies.stream()
            .collect(Collectors.groupingBy(ReviewReply::getCommentId));

        List<Long> memberIds = new ArrayList<>();
        comments.forEach(c -> memberIds.add(c.getMemberId()));
        allReplies.forEach(r -> {
            memberIds.add(r.getMemberId());
            if (r.getReplyToMemberId() != null) {
                memberIds.add(r.getReplyToMemberId());
            }
        });

        Map<Long, Member> memberMap = memberJpaRepository.findAllById(memberIds).stream()
            .collect(Collectors.toMap(Member::getId, m -> m));

        List<CommentResponse> commentResponses = comments.stream()
            .map(comment -> {
                Member member = memberMap.get(comment.getMemberId());
                List<ReviewReply> replies = repliesByCommentId.getOrDefault(comment.getId(), List.of());
                List<ReplyResponse> replyResponses = replies.stream()
                    .map(reply -> convertToReplyResponse(
                        reply,
                        memberMap.get(reply.getMemberId()),
                        reply.getReplyToMemberId() != null ? memberMap.get(reply.getReplyToMemberId()) : null
                    ))
                    .toList();
                return convertToCommentResponse(comment, member, replyResponses);
            })
            .toList();

        int totalCount = comments.size() + allReplies.size();
        return new CommentListResponse(commentResponses, totalCount);
    }

    private CommentResponse convertToCommentResponse(ReviewComment comment, Member member, List<ReplyResponse> replies) {
        return CommentResponse.builder()
            .id(comment.getId())
            .reviewId(comment.getReviewId())
            .memberId(comment.getMemberId())
            .memberNickname(member != null ? member.getNickname() : null)
            .memberProfileImageUrl(member != null ? member.getProfileImageUrl() : null)
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .replies(replies)
            .build();
    }

    private ReplyResponse convertToReplyResponse(ReviewReply reply, Member member, Member replyToMember) {
        return ReplyResponse.builder()
            .id(reply.getId())
            .commentId(reply.getCommentId())
            .memberId(reply.getMemberId())
            .memberNickname(member != null ? member.getNickname() : null)
            .memberProfileImageUrl(member != null ? member.getProfileImageUrl() : null)
            .replyToMemberId(reply.getReplyToMemberId())
            .replyToMemberNickname(replyToMember != null ? replyToMember.getNickname() : null)
            .content(reply.getContent())
            .createdAt(reply.getCreatedAt())
            .build();
    }

    public Optional<ReviewProductResponse> findReviewProduct(Long reviewId) {
        Optional<ReviewDetailDto> reviewDetailOpt = reviewCoreService.findReviewDetail(reviewId);
        if (reviewDetailOpt.isEmpty()) {
            return Optional.empty();
        }

        ReviewDetailDto reviewDetail = reviewDetailOpt.get();
        Review review = reviewCoreService.findById(reviewId);
        if (review == null) {
            return Optional.empty();
        }

        return productCoreService.findProductById(review.getProductId())
            .map(product -> {
                Integer price = product.getDiscountPrice() != null 
                    ? product.getDiscountPrice() 
                    : product.getOriginalPrice();
                
                return ReviewProductResponse.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .productImageUrl(product.getImageUrl())
                    .productPrice(price)
                    .reviewId(reviewDetail.getId())
                    .content(reviewDetail.getContent())
                    .totalRating(reviewDetail.getTotalRating())
                    .tasteRating(reviewDetail.getTasteRating())
                    .amountRating(reviewDetail.getAmountRating())
                    .priceRating(reviewDetail.getPriceRating())
                    .atmosphereRating(reviewDetail.getAtmosphereRating())
                    .kindnessRating(reviewDetail.getKindnessRating())
                    .hygieneRating(reviewDetail.getHygieneRating())
                    .willRevisit(reviewDetail.getWillRevisit())
                    .memberId(reviewDetail.getMemberId())
                    .memberNickname(reviewDetail.getMemberNickname())
                    .memberProfileImageUrl(reviewDetail.getMemberProfileImageUrl())
                    .createdAt(reviewDetail.getCreatedAt())
                    .imageUrls(reviewDetail.getImageUrls())
                    .tagNames(reviewDetail.getTagNames())
                    .build();
            })
            .or(() -> Optional.of(
                ReviewProductResponse.builder()
                    .productId(null)
                    .productName(null)
                    .productImageUrl(null)
                    .productPrice(null)
                    .reviewId(reviewDetail.getId())
                    .content(reviewDetail.getContent())
                    .totalRating(reviewDetail.getTotalRating())
                    .tasteRating(reviewDetail.getTasteRating())
                    .amountRating(reviewDetail.getAmountRating())
                    .priceRating(reviewDetail.getPriceRating())
                    .atmosphereRating(reviewDetail.getAtmosphereRating())
                    .kindnessRating(reviewDetail.getKindnessRating())
                    .hygieneRating(reviewDetail.getHygieneRating())
                    .willRevisit(reviewDetail.getWillRevisit())
                    .memberId(reviewDetail.getMemberId())
                    .memberNickname(reviewDetail.getMemberNickname())
                    .memberProfileImageUrl(reviewDetail.getMemberProfileImageUrl())
                    .createdAt(reviewDetail.getCreatedAt())
                    .imageUrls(reviewDetail.getImageUrls())
                    .tagNames(reviewDetail.getTagNames())
                    .build()
            ));
    }
}
