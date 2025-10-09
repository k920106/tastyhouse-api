package com.tastyhouse.core.repository.company;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.company.Company;
import com.tastyhouse.core.entity.company.QCompany;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Company> findAll() {
        QCompany company = QCompany.company;

        return queryFactory
            .selectFrom(company)
            .fetch();
    }

    @Override
    public Optional<Company> findById(Long id) {
        QCompany company = QCompany.company;

        Company result = queryFactory
            .selectFrom(company)
            .where(company.id.eq(id))
            .fetchOne();

        return Optional.ofNullable(result);
    }
}
