package com.tastyhouse.webapi.grade;

import com.tastyhouse.core.entity.rank.MemberReviewRank;
import com.tastyhouse.core.entity.rank.RankType;
import com.tastyhouse.core.entity.user.MemberGrade;
import com.tastyhouse.core.repository.rank.MemberReviewRankJpaRepository;
import com.tastyhouse.core.service.MemberGradeService;
import com.tastyhouse.webapi.grade.response.GradeInfoItem;
import com.tastyhouse.webapi.member.response.MyGradeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GradeService {

    private final MemberReviewRankJpaRepository memberReviewRankJpaRepository;

    /**
     * 등급별 최소 리뷰 기준 반환
     */
    private int getMinReviewCount(MemberGrade grade) {
        return switch (grade) {
            case NEWCOMER -> 0;
            case ACTIVE -> MemberGradeService.ACTIVE_THRESHOLD;
            case INSIDER -> MemberGradeService.INSIDER_THRESHOLD;
            case GOURMET -> MemberGradeService.GOURMET_THRESHOLD;
            case TEHA -> MemberGradeService.TEHA_THRESHOLD;
        };
    }

    /**
     * 등급별 최대 리뷰 기준 반환 (최고 등급 TEHA는 null)
     */
    private Integer getMaxReviewCount(MemberGrade grade) {
        return switch (grade) {
            case NEWCOMER -> MemberGradeService.ACTIVE_THRESHOLD - 1;
            case ACTIVE -> MemberGradeService.INSIDER_THRESHOLD - 1;
            case INSIDER -> MemberGradeService.GOURMET_THRESHOLD - 1;
            case GOURMET -> MemberGradeService.TEHA_THRESHOLD - 1;
            case TEHA -> null;
        };
    }

    /**
     * 전체 등급 세부 조건 목록 조회
     */
    public List<GradeInfoItem> getGradeInfoList() {
        return Arrays.stream(MemberGrade.values())
                .map(grade -> GradeInfoItem.builder()
                        .grade(grade)
                        .displayName(grade.getDisplayName())
                        .minReviewCount(getMinReviewCount(grade))
                        .maxReviewCount(getMaxReviewCount(grade))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 내 등급 정보 조회
     */
    public MyGradeResponse getMyGrade(Long memberId) {
        int currentReviewCount = memberReviewRankJpaRepository
                .findLatestByMemberIdAndRankType(memberId, RankType.ALL)
                .map(MemberReviewRank::getReviewCount)
                .orElse(0);

        MemberGrade currentGrade = MemberGradeService.fromReviewCount(currentReviewCount);
        MemberGrade nextGrade = currentGrade.isHigherThanOrEqual(MemberGrade.TEHA) ? null : MemberGrade.fromLevel(currentGrade.getLevel() + 1);

        int reviewsNeeded = 0;
        if (nextGrade != null) {
            reviewsNeeded = getMinReviewCount(nextGrade) - currentReviewCount;
        }

        return MyGradeResponse.builder()
                .currentGrade(currentGrade)
                .currentGradeDisplayName(currentGrade.getDisplayName())
                .nextGrade(nextGrade)
                .nextGradeDisplayName(nextGrade != null ? nextGrade.getDisplayName() : null)
                .currentReviewCount(currentReviewCount)
                .reviewsNeededForNextGrade(reviewsNeeded)
                .build();
    }
}
