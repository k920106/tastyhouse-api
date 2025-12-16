package com.tastyhouse.core.repository.rank;

import com.tastyhouse.core.entity.rank.MemberReviewRank;
import com.tastyhouse.core.entity.rank.RankType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberReviewRankJpaRepository extends JpaRepository<MemberReviewRank, Long> {

    Optional<MemberReviewRank> findByMemberIdAndRankTypeAndBaseDate(
        Long memberId,
        RankType rankType,
        LocalDate baseDate
    );

    List<MemberReviewRank> findByRankTypeAndBaseDateOrderByRankNoAsc(
        RankType rankType,
        LocalDate baseDate
    );

    void deleteByRankTypeAndBaseDate(RankType rankType, LocalDate baseDate);
}