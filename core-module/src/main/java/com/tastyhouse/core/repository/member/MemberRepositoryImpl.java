package com.tastyhouse.core.repository.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.user.Member;
import com.tastyhouse.core.entity.user.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Member> findByUsername(String username) {
        QMember member = QMember.member;

        Member result = queryFactory
            .selectFrom(member)
            .where(member.username.eq(username))
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<Member> findAllMembers(Pageable pageable) {
        QMember member = QMember.member;

        List<Member> content = queryFactory
            .selectFrom(member)
            .orderBy(member.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .select(member.count())
            .from(member)
            .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
