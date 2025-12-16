package com.tastyhouse.core.repository.rank;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.rank.QMemberReviewRank;
import com.tastyhouse.core.entity.rank.RankType;
import com.tastyhouse.core.entity.rank.dto.MemberRankDto;
import com.tastyhouse.core.entity.rank.dto.QMemberRankDto;
import com.tastyhouse.core.entity.user.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberReviewRankRepositoryImpl implements MemberReviewRankRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberRankDto> findMemberRankList(RankType rankType, LocalDate baseDate, int limit) {
        QMemberReviewRank rank = QMemberReviewRank.memberReviewRank;
        QMember member = QMember.member;

        return queryFactory
            .select(new QMemberRankDto(
                rank.memberId,
                member.nickname,
                member.profileImageUrl,
                rank.reviewCount,
                rank.rankNo,
                member.memberGrade
            ))
            .from(rank)
            .innerJoin(member).on(rank.memberId.eq(member.id))
            .where(
                rank.rankType.eq(rankType),
                rank.baseDate.eq(baseDate)
            )
            .orderBy(rank.rankNo.asc())
            .limit(limit)
            .fetch();
    }
}