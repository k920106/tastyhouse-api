package com.tastyhouse.core.repository.review;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.place.QPlace;
import com.tastyhouse.core.entity.place.QPlaceStation;
import com.tastyhouse.core.entity.product.QProduct;
import com.tastyhouse.core.entity.rank.dto.MemberReviewCountDto;
import com.tastyhouse.core.entity.rank.dto.QMemberReviewCountDto;
import com.tastyhouse.core.entity.review.QReview;
import com.tastyhouse.core.entity.review.QReviewImage;
import com.tastyhouse.core.entity.review.QReviewLike;
import com.tastyhouse.core.entity.review.dto.BestReviewListItemDto;
import com.tastyhouse.core.entity.review.dto.LatestReviewListItemDto;
import com.tastyhouse.core.entity.review.dto.QBestReviewListItemDto;
import com.tastyhouse.core.entity.review.dto.QLatestReviewListItemDto;
import com.tastyhouse.core.entity.review.dto.ReviewDetailDto;
import com.tastyhouse.core.entity.review.dto.QReviewDetailDto;
import com.tastyhouse.core.entity.user.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BestReviewListItemDto> findBestReviews(Pageable pageable) {
        QReview review = QReview.review;
        QPlace place = QPlace.place;
        QPlaceStation placeStation = QPlaceStation.placeStation;
        QReviewImage reviewImage = QReviewImage.reviewImage;
        QReviewImage subReviewImage = new QReviewImage("subReviewImage");

        JPAQuery<BestReviewListItemDto> query = queryFactory
            .select(new QBestReviewListItemDto(
                review.id,
                reviewImage.imageUrl,
                placeStation.stationName,
                review.totalRating,
                review.content
            ))
            .from(review)
            .innerJoin(place).on(review.placeId.eq(place.id))
            .innerJoin(placeStation).on(place.stationId.eq(placeStation.id))
            .leftJoin(reviewImage).on(
                reviewImage.reviewId.eq(review.id)
                .and(reviewImage.sort.eq(
                    JPAExpressions
                        .select(subReviewImage.sort.min())
                        .from(subReviewImage)
                        .where(subReviewImage.reviewId.eq(review.id))
                ))
            )
            .where(review.isHidden.eq(false))
            .orderBy(review.totalRating.desc(), review.createdAt.desc());

        long total = query.fetch().size();

        List<BestReviewListItemDto> reviews = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(reviews, pageable, total);
    }

    @Override
    public Page<LatestReviewListItemDto> findLatestReviews(Pageable pageable) {
        QReview review = QReview.review;
        QPlace place = QPlace.place;
        QPlaceStation placeStation = QPlaceStation.placeStation;
        QReviewImage reviewImage = QReviewImage.reviewImage;
        QMember member = QMember.member;
        QProduct product = QProduct.product;

        JPAQuery<LatestReviewListItemDto> query = queryFactory
            .select(new QLatestReviewListItemDto(
                review.id,
                placeStation.stationName,
                review.totalRating,
                review.content,
                member.id,
                member.nickname,
                member.profileImageUrl,
                review.createdAt,
                product.id,
                product.name
            ))
            .from(review)
            .innerJoin(place).on(review.placeId.eq(place.id))
            .innerJoin(placeStation).on(place.stationId.eq(placeStation.id))
            .innerJoin(member).on(review.memberId.eq(member.id))
            .leftJoin(product).on(review.productId.eq(product.id))
            .where(review.isHidden.eq(false))
            .orderBy(review.createdAt.desc());

        long total = query.fetch().size();

        List<LatestReviewListItemDto> reviews = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        if (!reviews.isEmpty()) {
            List<Long> reviewIds = reviews.stream().map(LatestReviewListItemDto::getId).toList();
            Map<Long, List<String>> imageUrlsMap = findImageUrlsByReviewIds(reviewIds);
            reviews.forEach(r -> r.setImageUrls(imageUrlsMap.getOrDefault(r.getId(), List.of())));
        }

        return new PageImpl<>(reviews, pageable, total);
    }

    private Map<Long, List<String>> findImageUrlsByReviewIds(List<Long> reviewIds) {
        QReviewImage reviewImage = QReviewImage.reviewImage;

        List<com.querydsl.core.Tuple> results = queryFactory
            .select(reviewImage.reviewId, reviewImage.imageUrl)
            .from(reviewImage)
            .where(reviewImage.reviewId.in(reviewIds))
            .orderBy(reviewImage.sort.asc())
            .fetch();

        return results.stream()
            .collect(Collectors.groupingBy(
                tuple -> tuple.get(reviewImage.reviewId),
                Collectors.mapping(tuple -> tuple.get(reviewImage.imageUrl), Collectors.toList())
            ));
    }

    @Override
    public Page<LatestReviewListItemDto> findLatestReviewsByFollowing(List<Long> followingMemberIds, Pageable pageable) {
        QReview review = QReview.review;
        QPlace place = QPlace.place;
        QPlaceStation placeStation = QPlaceStation.placeStation;
        QMember member = QMember.member;
        QProduct product = QProduct.product;

        JPAQuery<LatestReviewListItemDto> query = queryFactory
            .select(new QLatestReviewListItemDto(
                review.id,
                placeStation.stationName,
                review.totalRating,
                review.content,
                member.id,
                member.nickname,
                member.profileImageUrl,
                review.createdAt,
                product.id,
                product.name
            ))
            .from(review)
            .innerJoin(place).on(review.placeId.eq(place.id))
            .innerJoin(placeStation).on(place.stationId.eq(placeStation.id))
            .innerJoin(member).on(review.memberId.eq(member.id))
            .leftJoin(product).on(review.productId.eq(product.id))
            .where(
                review.memberId.in(followingMemberIds),
                review.isHidden.eq(false)
            )
            .orderBy(review.createdAt.desc());

        long total = query.fetch().size();

        List<LatestReviewListItemDto> reviews = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        if (!reviews.isEmpty()) {
            List<Long> reviewIds = reviews.stream().map(LatestReviewListItemDto::getId).toList();
            Map<Long, List<String>> imageUrlsMap = findImageUrlsByReviewIds(reviewIds);
            reviews.forEach(r -> r.setImageUrls(imageUrlsMap.getOrDefault(r.getId(), List.of())));
        }

        return new PageImpl<>(reviews, pageable, total);
    }

    @Override
    public Page<LatestReviewListItemDto> findLatestReviewsByPlaceId(Long placeId, Integer rating, Pageable pageable, Boolean hasImage, String sortType) {
        QReview review = QReview.review;
        QPlace place = QPlace.place;
        QPlaceStation placeStation = QPlaceStation.placeStation;
        QReviewImage reviewImage = QReviewImage.reviewImage;
        QReviewLike reviewLike = QReviewLike.reviewLike;
        QMember member = QMember.member;
        QProduct product = QProduct.product;

        var whereClause = review.placeId.eq(placeId).and(review.isHidden.eq(false));
        if (rating != null) {
            if (rating == 5) {
                // rating이 5인 경우 정확히 5.0만
                whereClause = whereClause.and(review.totalRating.eq(5.0));
            } else {
                // rating이 3, 4인 경우 해당 범위 (예: 4 -> 4.0 ~ 4.9)
                whereClause = whereClause.and(
                    review.totalRating.goe(rating.doubleValue())
                        .and(review.totalRating.lt(rating.doubleValue() + 1.0))
                );
            }
        }

        // 이미지 필터링
        if (hasImage != null) {
            QReviewImage subReviewImage = new QReviewImage("subReviewImage");
            if (hasImage) {
                // 이미지가 있는 리뷰만
                whereClause = whereClause.and(
                    JPAExpressions
                        .selectOne()
                        .from(subReviewImage)
                        .where(subReviewImage.reviewId.eq(review.id))
                        .exists()
                );
            } else {
                // 이미지가 없는 리뷰만
                whereClause = whereClause.and(
                    JPAExpressions
                        .selectOne()
                        .from(subReviewImage)
                        .where(subReviewImage.reviewId.eq(review.id))
                        .notExists()
                );
            }
        }

        JPAQuery<LatestReviewListItemDto> query = queryFactory
            .select(new QLatestReviewListItemDto(
                review.id,
                placeStation.stationName,
                review.totalRating,
                review.content,
                member.id,
                member.nickname,
                member.profileImageUrl,
                review.createdAt,
                product.id,
                product.name
            ))
            .from(review)
            .innerJoin(place).on(review.placeId.eq(place.id))
            .innerJoin(placeStation).on(place.stationId.eq(placeStation.id))
            .innerJoin(member).on(review.memberId.eq(member.id))
            .leftJoin(product).on(review.productId.eq(product.id))
            .where(whereClause);

        // 정렬 로직
        if ("RECOMMENDED".equals(sortType)) {
            // 추천순: 좋아요 수 기준 (내림차순)
            // 좋아요 수를 계산하기 위해 leftJoin 사용
            QReviewLike subReviewLike = new QReviewLike("subReviewLike");
            query.leftJoin(subReviewLike).on(subReviewLike.reviewId.eq(review.id))
                .groupBy(review.id, placeStation.stationName, review.totalRating, review.content,
                    member.id, member.nickname, member.profileImageUrl, review.createdAt,
                    product.id, product.name)
                .orderBy(subReviewLike.count().desc(), review.createdAt.desc());
        } else if ("OLDEST".equals(sortType)) {
            // 오래된순
            query.orderBy(review.createdAt.asc());
        } else {
            // 최신순 (기본값)
            query.orderBy(review.createdAt.desc());
        }

        long total = query.fetch().size();

        List<LatestReviewListItemDto> reviews = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        if (!reviews.isEmpty()) {
            List<Long> reviewIds = reviews.stream().map(LatestReviewListItemDto::getId).toList();
            Map<Long, List<String>> imageUrlsMap = findImageUrlsByReviewIds(reviewIds);
            reviews.forEach(r -> r.setImageUrls(imageUrlsMap.getOrDefault(r.getId(), List.of())));
        }

        return new PageImpl<>(reviews, pageable, total);
    }

    @Override
    public List<MemberReviewCountDto> countReviewsByMemberWithPeriod(
        LocalDateTime startDate,
        LocalDateTime endDate
    ) {
        QReview review = QReview.review;

        return queryFactory
            .select(new QMemberReviewCountDto(
                review.memberId,
                review.count(),
                review.createdAt.max()
            ))
            .from(review)
            .where(
                review.createdAt.goe(startDate),
                review.createdAt.lt(endDate)
            )
            .groupBy(review.memberId)
            .orderBy(
                review.count().desc(),
                review.createdAt.max().asc(),
                review.memberId.asc()
            )
            .fetch();
    }

    @Override
    public Optional<ReviewDetailDto> findReviewDetail(Long reviewId) {
        QReview review = QReview.review;
        QPlace place = QPlace.place;
        QPlaceStation placeStation = QPlaceStation.placeStation;
        QMember member = QMember.member;

        ReviewDetailDto result = queryFactory
            .select(new QReviewDetailDto(
                review.id,
                place.id,
                place.name,
                placeStation.stationName,
                review.content,
                review.totalRating,
                review.tasteRating,
                review.amountRating,
                review.priceRating,
                review.atmosphereRating,
                review.kindnessRating,
                review.hygieneRating,
                review.willRevisit,
                member.id,
                member.nickname,
                member.profileImageUrl,
                review.createdAt
            ))
            .from(review)
            .innerJoin(place).on(review.placeId.eq(place.id))
            .innerJoin(placeStation).on(place.stationId.eq(placeStation.id))
            .innerJoin(member).on(review.memberId.eq(member.id))
            .where(
                review.id.eq(reviewId),
                review.isHidden.eq(false)
            )
            .fetchOne();

        if (result != null) {
            List<String> imageUrls = findImageUrlsByReviewId(reviewId);
            result.setImageUrls(imageUrls);
        }

        return Optional.ofNullable(result);
    }

    @Override
    public List<LatestReviewListItemDto> findReviewsByPlaceIdAndRating(Long placeId, Integer rating, int limit) {
        QReview review = QReview.review;
        QPlace place = QPlace.place;
        QPlaceStation placeStation = QPlaceStation.placeStation;
        QMember member = QMember.member;
        QProduct product = QProduct.product;

        var whereClause = review.placeId.eq(placeId).and(review.isHidden.eq(false));

        if (rating == 5) {
            // rating이 5인 경우 정확히 5.0만
            whereClause = whereClause.and(review.totalRating.eq(5.0));
        } else {
            // rating이 1, 2, 3, 4인 경우 해당 범위 (예: 4 -> 4.0 ~ 4.9)
            whereClause = whereClause.and(
                review.totalRating.goe(rating.doubleValue())
                    .and(review.totalRating.lt(rating.doubleValue() + 1.0))
            );
        }

        List<LatestReviewListItemDto> reviews = queryFactory
            .select(new QLatestReviewListItemDto(
                review.id,
                placeStation.stationName,
                review.totalRating,
                review.content,
                member.id,
                member.nickname,
                member.profileImageUrl,
                review.createdAt,
                product.id,
                product.name
            ))
            .from(review)
            .innerJoin(place).on(review.placeId.eq(place.id))
            .innerJoin(placeStation).on(place.stationId.eq(placeStation.id))
            .innerJoin(member).on(review.memberId.eq(member.id))
            .leftJoin(product).on(review.productId.eq(product.id))
            .where(whereClause)
            .orderBy(review.createdAt.desc())
            .limit(limit)
            .fetch();

        if (!reviews.isEmpty()) {
            List<Long> reviewIds = reviews.stream().map(LatestReviewListItemDto::getId).toList();
            Map<Long, List<String>> imageUrlsMap = findImageUrlsByReviewIds(reviewIds);
            reviews.forEach(r -> r.setImageUrls(imageUrlsMap.getOrDefault(r.getId(), List.of())));
        }

        return reviews;
    }

    private List<String> findImageUrlsByReviewId(Long reviewId) {
        QReviewImage reviewImage = QReviewImage.reviewImage;

        return queryFactory
            .select(reviewImage.imageUrl)
            .from(reviewImage)
            .where(reviewImage.reviewId.eq(reviewId))
            .orderBy(reviewImage.sort.asc())
            .fetch();
    }
}
