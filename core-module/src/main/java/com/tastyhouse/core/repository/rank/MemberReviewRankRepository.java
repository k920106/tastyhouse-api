package com.tastyhouse.core.repository.rank;

import com.tastyhouse.core.entity.rank.RankType;
import com.tastyhouse.core.entity.rank.dto.MemberRankDto;

import java.time.LocalDate;
import java.util.List;

public interface MemberReviewRankRepository {

    List<MemberRankDto> findMemberRankList(RankType rankType, LocalDate baseDate, int limit);
}