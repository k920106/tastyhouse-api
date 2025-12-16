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
import com.tastyhouse.core.entity.review.dto.QBestReviewListItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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
                review.title,
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
            .orderBy(review.totalRating.desc(), review.createdAt.desc());

        long total = query.fetch().size();

        List<BestReviewListItemDto> reviews = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

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
}
