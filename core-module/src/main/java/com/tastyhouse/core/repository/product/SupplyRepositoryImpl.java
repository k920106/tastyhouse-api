package com.tastyhouse.core.repository.product;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.product.Supply;
import com.tastyhouse.core.entity.product.QSupply;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SupplyRepositoryImpl implements SupplyRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Supply> findAll() {
        QSupply supply = QSupply.supply;

        return queryFactory
            .selectFrom(supply)
            .fetch();
    }
}
