package com.tastyhouse.core.repository.review;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.place.QPlace;
import com.tastyhouse.core.entity.place.QPlaceStation;
import com.tastyhouse.core.entity.rank.dto.MemberReviewCountDto;
import com.tastyhouse.core.entity.rank.dto.QMemberReviewCountDto;
import com.tastyhouse.core.entity.review.QReview;
import com.tastyhouse.core.entity.review.QReviewImage;
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

        JPAQuery<LatestReviewListItemDto> query = queryFactory
            .select(new QLatestReviewListItemDto(
                review.id,
                placeStation.stationName,
                review.totalRating,
                review.content,
                member.id,
                member.nickname,
                member.profileImageUrl,
                review.createdAt
            ))
            .from(review)
            .innerJoin(place).on(review.placeId.eq(place.id))
            .innerJoin(placeStation).on(place.stationId.eq(placeStation.id))
            .innerJoin(member).on(review.memberId.eq(member.id))
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

        JPAQuery<LatestReviewListItemDto> query = queryFactory
            .select(new QLatestReviewListItemDto(
                review.id,
                placeStation.stationName,
                review.totalRating,
                review.content,
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
    public Page<LatestReviewListItemDto> findLatestReviewsByPlaceId(Long placeId, Integer rating, Pageable pageable) {
        QReview review = QReview.review;
        QPlace place = QPlace.place;
        QPlaceStation placeStation = QPlaceStation.placeStation;
        QMember member = QMember.member;

        var whereClause = review.placeId.eq(placeId).and(review.isHidden.eq(false));
        if (rating != null) {
            whereClause = whereClause.and(review.totalRating.eq(rating.doubleValue()));
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
                review.createdAt
            ))
            .from(review)
            .innerJoin(place).on(review.placeId.eq(place.id))
            .innerJoin(placeStation).on(place.stationId.eq(placeStation.id))
            .innerJoin(member).on(review.memberId.eq(member.id))
            .where(whereClause)
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
