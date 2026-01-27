package com.tastyhouse.core.repository.notice;

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

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<NoticeListItemDto> findAllWithFilter(Pageable pageable) {
        QNotice notice = QNotice.notice;

        JPAQuery<NoticeListItemDto> query = queryFactory
            .select(new QNoticeListItemDto(
                notice.id,
                notice.title,
                notice.createdAt
            ))
            .from(notice)
            .where(notice.active.isTrue())
            .orderBy(notice.createdAt.desc());

        long total = query.fetch().size();

        List<NoticeListItemDto> notices = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(notices, pageable, total);
    }
}
