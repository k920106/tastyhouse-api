package com.tastyhouse.core.repository.product;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.place.QPlace;
import com.tastyhouse.core.entity.product.QProduct;
import com.tastyhouse.core.entity.product.QProductImage;
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
        QProductImage productImage = QProductImage.productImage;
        QProductImage subProductImage = new QProductImage("subProductImage");

        JPAQuery<TodayDiscountProductDto> query = queryFactory
            .select(new QTodayDiscountProductDto(
                product.id,
                place.name,
                product.name,
                productImage.imageUrl,
                product.originalPrice,
                product.discountPrice,
                product.discountRate
            ))
            .from(product)
            .innerJoin(place).on(product.placeId.eq(place.id))
            .leftJoin(productImage).on(
                productImage.productId.eq(product.id)
                .and(productImage.isActive.eq(true))
                .and(productImage.sort.eq(
                    JPAExpressions
                        .select(subProductImage.sort.min())
                        .from(subProductImage)
                        .where(subProductImage.productId.eq(product.id)
                            .and(subProductImage.isActive.eq(true)))
                ))
            )
            .where(product.discountPrice.isNotNull()
                .and(product.isActive.eq(true)))
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
        QProductImage productImage = QProductImage.productImage;
        QProductImage subProductImage = new QProductImage("subProductImage");

        return queryFactory
            .select(new QProductSimpleDto(
                product.id,
                place.name,
                product.name,
                productImage.imageUrl,
                product.originalPrice,
                product.discountPrice,
                product.discountRate
            ))
            .from(product)
            .innerJoin(place).on(place.id.eq(product.placeId))
            .leftJoin(productImage).on(
                productImage.productId.eq(product.id)
                .and(productImage.isActive.eq(true))
                .and(productImage.sort.eq(
                    JPAExpressions
                        .select(subProductImage.sort.min())
                        .from(subProductImage)
                        .where(subProductImage.productId.eq(product.id)
                            .and(subProductImage.isActive.eq(true)))
                ))
            )
            .where(product.placeId.eq(placeId)
                .and(product.isActive.eq(true)))
            .fetch();
    }
}
