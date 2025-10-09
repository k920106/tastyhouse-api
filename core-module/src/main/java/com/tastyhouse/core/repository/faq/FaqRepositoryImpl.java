package com.tastyhouse.core.repository.faq;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.faq.QFaq;
import com.tastyhouse.core.entity.faq.dto.FaqListItemDto;
import com.tastyhouse.core.entity.faq.dto.QFaqListItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FaqRepositoryImpl implements FaqRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<FaqListItemDto> findAllWithFilter(Long companyId, String title, Boolean active, Pageable pageable) {
        QFaq faq = QFaq.faq;

        BooleanBuilder builder = new BooleanBuilder();

        if (companyId != null) {
            builder.and(faq.companyId.eq(companyId));
        }

        if (StringUtils.hasText(title)) {
            builder.and(faq.title.containsIgnoreCase(title));
        }

        if (active != null) {
            builder.and(faq.active.eq(active));
        }

        JPAQuery<FaqListItemDto> query = queryFactory
            .select(new QFaqListItemDto(faq.id, faq.companyId, faq.title, faq.content,
                                        faq.active, faq.sort, faq.createdAt, faq.updatedAt))
            .from(faq)
            .where(builder)
            .orderBy(faq.sort.asc(), faq.id.desc());

        long total = query.fetch().size();

        List<FaqListItemDto> faqs = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(faqs, pageable, total);
    }
}
