package com.tastyhouse.webapi.review;

import com.tastyhouse.core.entity.place.Tag;
import com.tastyhouse.core.entity.review.Review;
import com.tastyhouse.core.entity.review.ReviewComment;
import com.tastyhouse.core.entity.review.ReviewImage;
import com.tastyhouse.core.entity.review.ReviewReply;
import com.tastyhouse.core.entity.review.ReviewTag;
import com.tastyhouse.core.entity.review.dto.BestReviewListItemDto;
import com.tastyhouse.core.entity.review.dto.LatestReviewListItemDto;
import com.tastyhouse.core.entity.review.dto.ReviewDetailDto;
import com.tastyhouse.core.entity.user.Member;
import com.tastyhouse.core.exception.AccessDeniedException;
import com.tastyhouse.core.exception.BusinessException;
import com.tastyhouse.core.exception.EntityNotFoundException;
import com.tastyhouse.core.exception.ErrorCode;
import com.tastyhouse.core.entity.order.OrderItem;
import com.tastyhouse.core.entity.product.Product;
import com.tastyhouse.core.repository.member.MemberJpaRepository;
import com.tastyhouse.core.repository.order.OrderItemJpaRepository;
import com.tastyhouse.core.repository.place.TagJpaRepository;
import com.tastyhouse.core.common.PageResult;
import com.tastyhouse.core.service.PlaceCoreService;
import com.tastyhouse.core.service.ProductCoreService;
import com.tastyhouse.core.service.ReviewCoreService;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.review.request.ReviewCreateRequest;
import com.tastyhouse.webapi.review.request.ReviewType;
import com.tastyhouse.webapi.review.request.ReviewUpdateRequest;
import com.tastyhouse.webapi.review.response.BestReviewListItem;
import com.tastyhouse.webapi.review.response.CommentListResponse;
import com.tastyhouse.webapi.review.response.CommentResponse;
import com.tastyhouse.webapi.review.response.LatestReviewListItem;
import com.tastyhouse.webapi.review.response.ReplyResponse;
import com.tastyhouse.webapi.review.response.ReviewDetailResponse;
import com.tastyhouse.webapi.review.response.ReviewLikeStatusResponse;
import com.tastyhouse.webapi.review.response.ReviewProductResponse;
import com.tastyhouse.webapi.review.response.ReviewResponse;
import com.tastyhouse.webapi.review.response.ReviewWriteInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewCoreService reviewCoreService;
    private final ProductCoreService productCoreService;
    private final PlaceCoreService placeCoreService;
    private final MemberJpaRepository memberJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final TagJpaRepository tagJpaRepository;
    private final com.tastyhouse.core.repository.file.UploadedFileJpaRepository uploadedFileJpaRepository;

    public PageResult<BestReviewListItem> findBestReviewList(PageRequest pageRequest) {
        return reviewCoreService.findBestReviewsWithPagination(
            pageRequest.getPage(), pageRequest.getSize()
        ).map(this::convertToBestReviewListItem);
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
        PageResult<LatestReviewListItemDto> coreResult;

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

        return coreResult.map(this::convertToLatestReviewListItem);
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

    @Transactional
    public boolean toggleReviewLike(Long reviewId, Long memberId) {
        return reviewCoreService.toggleReviewLike(reviewId, memberId);
    }

    @Transactional
    public CommentResponse createComment(Long reviewId, Long memberId, String content) {
        ReviewComment comment = reviewCoreService.createComment(reviewId, memberId, content);
        Member member = memberJpaRepository.findById(memberId).orElse(null);
        return convertToCommentResponse(comment, member, List.of());
    }

    @Transactional
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
        String memberProfileImageUrl = null;
        if (member != null && member.getProfileImageFileId() != null) {
            memberProfileImageUrl = uploadedFileJpaRepository.findById(member.getProfileImageFileId())
                .map(file -> file.getFilePath())
                .orElse(null);
        }

        return CommentResponse.builder()
            .id(comment.getId())
            .reviewId(comment.getReviewId())
            .memberId(comment.getMemberId())
            .memberNickname(member != null ? member.getNickname() : null)
            .memberProfileImageUrl(memberProfileImageUrl)
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .replies(replies)
            .build();
    }

    private ReplyResponse convertToReplyResponse(ReviewReply reply, Member member, Member replyToMember) {
        String memberProfileImageUrl = null;
        if (member != null && member.getProfileImageFileId() != null) {
            memberProfileImageUrl = uploadedFileJpaRepository.findById(member.getProfileImageFileId())
                .map(file -> file.getFilePath())
                .orElse(null);
        }

        return ReplyResponse.builder()
            .id(reply.getId())
            .commentId(reply.getCommentId())
            .memberId(reply.getMemberId())
            .memberNickname(member != null ? member.getNickname() : null)
            .memberProfileImageUrl(memberProfileImageUrl)
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
                
                String productImageUrl = getFirstImageUrl(product.getId());
                
                return ReviewProductResponse.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .productImageUrl(productImageUrl)
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

    private String getFirstImageUrl(Long productId) {
        return productCoreService.getFirstImageUrl(productId);
    }

    public ReviewWriteInfoResponse getReviewWriteInfo(Long orderItemId, Long memberId) {
        OrderItem orderItem = orderItemJpaRepository.findById(orderItemId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_ORDER_ITEM_NOT_FOUND));

        // 본인 주문인지 검증 (OrderItem의 orderId로 Order 접근은 OrderCoreService가 필요하나,
        // 여기서는 Product 정보 조회 후 리뷰 작성 여부 확인으로 처리)
        Product product = productCoreService.findProductById(orderItem.getProductId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_PRODUCT_NOT_FOUND));

        Integer price = product.getDiscountPrice() != null
                ? product.getDiscountPrice()
                : product.getOriginalPrice();

        boolean isReviewed = reviewCoreService.isReviewedByOrderAndProduct(
                orderItem.getOrderId(), orderItem.getProductId(), memberId);

        return ReviewWriteInfoResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productImageUrl(getFirstImageUrl(product.getId()))
                .productPrice(price)
                .orderId(orderItem.getOrderId())
                .isReviewed(isReviewed)
                .build();
    }

    @Transactional
    public ReviewResponse createReview(Long memberId, ReviewCreateRequest request) {
        // 주문 기반 리뷰인 경우 중복 작성 검증
        Long orderId = null;
        if (request.orderItemId() != null) {
            OrderItem orderItem = orderItemJpaRepository.findById(request.orderItemId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_ORDER_ITEM_NOT_FOUND));

            orderId = orderItem.getOrderId();

            if (reviewCoreService.isReviewedByOrderAndProduct(orderId, request.productId(), memberId)) {
                throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
            }
        }

        Product product = productCoreService.findProductById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_PRODUCT_NOT_FOUND));

        double totalRating = (request.tasteRating() + request.amountRating() + request.priceRating()) / 3.0;

        Review review = Review.builder()
                .placeId(product.getPlaceId())
                .productId(product.getId())
                .memberId(memberId)
                .content(request.content())
                .totalRating(Math.round(totalRating * 10.0) / 10.0)
                .tasteRating(request.tasteRating().doubleValue())
                .amountRating(request.amountRating().doubleValue())
                .priceRating(request.priceRating().doubleValue())
                .orderId(orderId)
                .build();

        Review savedReview = reviewCoreService.saveReview(review);

        List<String> imageUrls = saveReviewImages(savedReview.getId(), request.imageUrls());
        List<String> tags = saveReviewTags(savedReview.getId(), request.tags());

        return ReviewResponse.builder()
                .reviewId(savedReview.getId())
                .productId(savedReview.getProductId())
                .tasteRating(savedReview.getTasteRating())
                .amountRating(savedReview.getAmountRating())
                .priceRating(savedReview.getPriceRating())
                .totalRating(savedReview.getTotalRating())
                .content(savedReview.getContent())
                .imageUrls(imageUrls)
                .tags(tags)
                .createdAt(savedReview.getCreatedAt())
                .build();
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, Long memberId, ReviewUpdateRequest request) {
        Review review = reviewCoreService.findReviewByIdAndMemberId(reviewId, memberId)
                .orElseThrow(() -> new AccessDeniedException(ErrorCode.REVIEW_ACCESS_DENIED));

        double totalRating = (request.tasteRating() + request.amountRating() + request.priceRating()) / 3.0;

        review.updateContent(
                request.content(),
                Math.round(totalRating * 10.0) / 10.0,
                request.tasteRating().doubleValue(),
                request.amountRating().doubleValue(),
                request.priceRating().doubleValue(),
                null, null, null, null
        );

        reviewCoreService.deleteReviewImages(reviewId);
        reviewCoreService.deleteReviewTags(reviewId);

        List<String> imageUrls = saveReviewImages(reviewId, request.imageUrls());
        List<String> tags = saveReviewTags(reviewId, request.tags());

        return ReviewResponse.builder()
                .reviewId(review.getId())
                .productId(review.getProductId())
                .tasteRating(review.getTasteRating())
                .amountRating(review.getAmountRating())
                .priceRating(review.getPriceRating())
                .totalRating(review.getTotalRating())
                .content(review.getContent())
                .imageUrls(imageUrls)
                .tags(tags)
                .createdAt(review.getCreatedAt())
                .build();
    }

    @Transactional
    public void deleteReview(Long reviewId, Long memberId) {
        reviewCoreService.findReviewByIdAndMemberId(reviewId, memberId)
                .orElseThrow(() -> new AccessDeniedException(ErrorCode.REVIEW_ACCESS_DENIED));

        reviewCoreService.deleteReviewImages(reviewId);
        reviewCoreService.deleteReviewTags(reviewId);
        reviewCoreService.deleteReview(reviewId);
    }

    private List<String> saveReviewImages(Long reviewId, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return List.of();
        }
        List<ReviewImage> images = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            images.add(new ReviewImage(reviewId, imageUrls.get(i), i + 1));
        }
        reviewCoreService.saveReviewImages(images);
        return imageUrls;
    }

    private List<String> saveReviewTags(Long reviewId, List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return List.of();
        }
        List<ReviewTag> reviewTags = tagNames.stream()
                .map(tagName -> {
                    Tag tag = tagJpaRepository.findByTagName(tagName)
                            .orElseGet(() -> tagJpaRepository.save(new Tag(tagName)));
                    return new ReviewTag(reviewId, tag.getId());
                })
                .toList();
        reviewCoreService.saveReviewTags(reviewTags);
        return tagNames;
    }
}
