package com.tastyhouse.core.repository.notice;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.notice.QNotice;
import com.tastyhouse.core.entity.notice.dto.NoticeListItemDto;
import com.tastyhouse.core.entity.notice.dto.QNoticeListItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<NoticeListItemDto> findAllWithFilter(Long companyId, String title, Boolean active, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        QNotice notice = QNotice.notice;

        BooleanBuilder builder = new BooleanBuilder();

        if (companyId != null) {
            builder.and(notice.companyId.eq(companyId));
        }

        if (StringUtils.hasText(title)) {
            builder.and(notice.title.containsIgnoreCase(title));
        }

        if (active != null) {
            builder.and(notice.active.eq(active));
        }

        if (startDate != null) {
            builder.and(notice.createdAt.goe(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            builder.and(notice.createdAt.loe(endDate.atTime(23, 59, 59)));
        }

        JPAQuery<NoticeListItemDto> query = queryFactory
            .select(new QNoticeListItemDto(notice.id, notice.title, notice.content, notice.createdAt, notice.active))
            .from(notice)
            .where(builder)
            .orderBy(notice.top.desc(), notice.id.desc());

        long total = query.fetch().size();

        List<NoticeListItemDto> notices = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(notices, pageable, total);
    }
}
