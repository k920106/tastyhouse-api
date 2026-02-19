package com.tastyhouse.core.repository.partnership;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.partnership.PartnershipRequest;
import com.tastyhouse.core.entity.partnership.QPartnershipRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PartnershipRepositoryImpl implements PartnershipRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PartnershipRequest> findAllOrderByCreatedAtDesc() {
        QPartnershipRequest partnershipRequest = QPartnershipRequest.partnershipRequest;

        return queryFactory
            .selectFrom(partnershipRequest)
            .orderBy(partnershipRequest.createdAt.desc())
            .fetch();
    }

    @Override
    public Page<PartnershipRequest> findAllOrderByCreatedAtDesc(Pageable pageable) {
        QPartnershipRequest partnershipRequest = QPartnershipRequest.partnershipRequest;

        List<PartnershipRequest> content = queryFactory
            .selectFrom(partnershipRequest)
            .orderBy(partnershipRequest.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .select(partnershipRequest.count())
            .from(partnershipRequest)
            .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
