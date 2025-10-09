package com.tastyhouse.core.repository.product;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.product.Brand;
import com.tastyhouse.core.entity.product.QBrand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BrandRepositoryImpl implements BrandRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Brand> findAll() {
        QBrand brand = QBrand.brand;

        return queryFactory
            .selectFrom(brand)
            .fetch();
    }
}
