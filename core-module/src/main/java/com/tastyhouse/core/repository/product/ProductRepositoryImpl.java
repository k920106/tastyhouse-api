package com.tastyhouse.core.repository.product;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.company.QCompany;
import com.tastyhouse.core.entity.product.QBrand;
import com.tastyhouse.core.entity.product.QProduct;
import com.tastyhouse.core.entity.product.QSupply;
import com.tastyhouse.core.entity.product.dto.ProductListItemDto;
import com.tastyhouse.core.entity.product.dto.QProductListItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductListItemDto> findAllWithFilterAsDto(Long companyId, String productCode, String name,
                                                            Long brandId, Long supplyId, Boolean display, Pageable pageable) {
        QProduct product = QProduct.product;
        QCompany company = QCompany.company;
        QBrand brand = QBrand.brand;
        QSupply supply = QSupply.supply;

        BooleanBuilder builder = new BooleanBuilder();

        if (companyId != null) {
            builder.and(product.companyId.eq(companyId));
        }

        if (StringUtils.hasText(productCode)) {
            builder.and(product.productCode.containsIgnoreCase(productCode));
        }

        if (StringUtils.hasText(name)) {
            builder.and(product.name.containsIgnoreCase(name));
        }

        if (brandId != null) {
            builder.and(product.brandId.eq(brandId));
        }

        if (supplyId != null) {
            builder.and(product.supplyId.eq(supplyId));
        }

        if (display != null) {
            builder.and(product.display.eq(display));
        }

        JPAQuery<ProductListItemDto> query = queryFactory
            .select(new QProductListItemDto(
                product.id,
                product.companyId,
                product.productCode,
                product.name,
                product.brandId,
                product.supplyId,
                product.validityPeriod,
                product.exhibitionPrice,
                product.regularPrice,
                product.supplyPrice,
                product.display,
                product.sort,
                product.createdAt,
                product.updatedAt
            ))
            .from(product)
            .where(builder)
            .orderBy(product.sort.asc(), product.id.desc());

        long total = query.fetch().size();

        List<ProductListItemDto> products = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(products, pageable, total);
    }
}
