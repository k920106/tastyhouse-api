package com.tastyhouse.core.repository.rank;

import com.tastyhouse.core.entity.rank.MemberReviewRank;
import com.tastyhouse.core.entity.rank.RankType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberReviewRankJpaRepository extends JpaRepository<MemberReviewRank, Long> {

    Optional<MemberReviewRank> findByMemberIdAndRankTypeAndBaseDate(
        Long memberId,
        RankType rankType,
        LocalDate baseDate
    );

    @Query("SELECT mrr FROM MemberReviewRank mrr WHERE mrr.memberId = :memberId AND mrr.rankType = :rankType ORDER BY mrr.baseDate DESC LIMIT 1")
    Optional<MemberReviewRank> findLatestByMemberIdAndRankType(
        @Param("memberId") Long memberId,
        @Param("rankType") RankType rankType
    );

    List<MemberReviewRank> findByRankTypeAndBaseDateOrderByRankNoAsc(
        RankType rankType,
        LocalDate baseDate
    );

    void deleteByRankTypeAndBaseDate(RankType rankType, LocalDate baseDate);
}