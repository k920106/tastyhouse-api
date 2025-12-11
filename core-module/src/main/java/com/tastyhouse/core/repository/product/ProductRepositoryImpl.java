package com.tastyhouse.core.repository.product;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.place.QPlace;
import com.tastyhouse.core.entity.product.QProduct;
import com.tastyhouse.core.entity.product.dto.ProductSimpleDto;
import com.tastyhouse.core.entity.product.dto.QProductSimpleDto;
import com.tastyhouse.core.entity.product.dto.QTodayDiscountProductDto;
import com.tastyhouse.core.entity.product.dto.TodayDiscountProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<TodayDiscountProductDto> findTodayDiscountProducts(Pageable pageable) {
        QProduct product = QProduct.product;
        QPlace place = QPlace.place;

        JPAQuery<TodayDiscountProductDto> query = queryFactory
            .select(new QTodayDiscountProductDto(
                product.id,
                place.placeName,
                product.name,
                product.imageUrl,
                product.originalPrice,
                product.discountPrice,
                product.discountRate
            ))
            .from(product)
            .innerJoin(place).on(product.placeId.eq(place.id))
            .where(product.discountPrice.isNotNull())
            .orderBy(product.discountRate.desc());

        long total = query.fetch().size();

        List<TodayDiscountProductDto> products = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(products, pageable, total);
    }

    @Override
    public List<ProductSimpleDto> findProductsByPlaceId(Long placeId) {
        QProduct product = QProduct.product;
        QPlace place = QPlace.place;

        return queryFactory
            .select(new QProductSimpleDto(
                product.id,
                place.placeName,
                product.name,
                product.imageUrl,
                product.originalPrice,
                product.discountPrice,
                product.discountRate
            ))
            .from(product)
            .innerJoin(place).on(product.placeId.eq(place.id))
            .where(product.placeId.eq(placeId))
            .fetch();
    }
}
