package com.tastyhouse.core.entity.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 회원 등급
 * - 활동량, 리뷰 수 등에 따라 결정되는 회원의 등급
 */
@Getter
@RequiredArgsConstructor
public enum MemberGrade {
    NEWCOMER(1, "신입멤버"),    // 가장 낮은 등급
    ACTIVE(2, "열심멤버"),
    INSIDER(3, "인싸멤버"),
    GOURMET(4, "미식멤버"),
    TEHA(5, "테하멤버");        // 가장 높은 등급

    private final int level;
    private final String displayName;

    /**
     * level 값으로 MemberGrade를 찾습니다
     * @param level 등급 레벨 (1-5)
     * @return MemberGrade
     * @throws IllegalArgumentException 유효하지 않은 level인 경우
     */
    public static MemberGrade fromLevel(int level) {
        return Arrays.stream(values())
                .filter(grade -> grade.level == level)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid member grade level: " + level));
    }

    /**
     * 다른 등급보다 높은지 확인
     */
    public boolean isHigherThan(MemberGrade other) {
        return this.level > other.level;
    }

    /**
     * 다른 등급보다 낮은지 확인
     */
    public boolean isLowerThan(MemberGrade other) {
        return this.level < other.level;
    }

    /**
     * 다른 등급과 같거나 높은지 확인
     */
    public boolean isHigherThanOrEqual(MemberGrade other) {
        return this.level >= other.level;
    }
}