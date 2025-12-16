package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.rank.dto.MemberReviewCountDto;
import com.tastyhouse.core.entity.user.MemberGrade;
import com.tastyhouse.core.repository.review.ReviewRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 회원 등급 관리 서비스
 * - 리뷰 개수 기준으로 회원 등급을 계산하고 업데이트
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberGradeService {

    private final ReviewRepository reviewRepository;
    private final EntityManager entityManager;

    /**
     * 등급 기준 (리뷰 개수)
     * - NEWCOMER: 0~4개
     * - ACTIVE: 5~19개
     * - INSIDER: 20~49개
     * - GOURMET: 50~99개
     * - TEHA: 100개 이상
     */
    private static final int ACTIVE_THRESHOLD = 5;
    private static final int INSIDER_THRESHOLD = 20;
    private static final int GOURMET_THRESHOLD = 50;
    private static final int TEHA_THRESHOLD = 100;

    /**
     * 모든 회원의 등급을 리뷰 개수 기준으로 업데이트
     */
    @Transactional
    public void updateAllMemberGrades() {
        log.info("=== 회원 등급 업데이트 시작 ===");

        // 전체 기간 리뷰 개수 조회
        LocalDateTime startDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.now();

        List<MemberReviewCountDto> reviewCounts = reviewRepository
                .countReviewsByMemberWithPeriod(startDate, endDate);

        log.info("리뷰 작성 회원 수: {}", reviewCounts.size());

        // 등급별로 회원 ID 그룹핑
        Map<MemberGrade, List<Long>> gradeGroups = groupMembersByGrade(reviewCounts);

        // 등급별로 벌크 업데이트
        int totalUpdated = 0;
        for (Map.Entry<MemberGrade, List<Long>> entry : gradeGroups.entrySet()) {
            MemberGrade grade = entry.getKey();
            List<Long> memberIds = entry.getValue();

            if (!memberIds.isEmpty()) {
                int updated = updateMemberGrades(memberIds, grade);
                totalUpdated += updated;
                log.info("등급 업데이트: {} - {} 명", grade.getDisplayName(), updated);
            }
        }

        log.info("=== 회원 등급 업데이트 완료: 총 {} 명 ===", totalUpdated);
    }

    /**
     * 리뷰 개수에 따라 회원을 등급별로 그룹핑
     */
    private Map<MemberGrade, List<Long>> groupMembersByGrade(List<MemberReviewCountDto> reviewCounts) {
        Map<MemberGrade, List<Long>> gradeGroups = new HashMap<>();

        for (MemberGrade grade : MemberGrade.values()) {
            gradeGroups.put(grade, reviewCounts.stream()
                    .filter(dto -> determineGrade(dto.getReviewCount().intValue()) == grade)
                    .map(MemberReviewCountDto::getMemberId)
                    .collect(Collectors.toList()));
        }

        return gradeGroups;
    }

    /**
     * 리뷰 개수로 등급 결정
     */
    private MemberGrade determineGrade(int reviewCount) {
        if (reviewCount >= TEHA_THRESHOLD) {
            return MemberGrade.TEHA;
        } else if (reviewCount >= GOURMET_THRESHOLD) {
            return MemberGrade.GOURMET;
        } else if (reviewCount >= INSIDER_THRESHOLD) {
            return MemberGrade.INSIDER;
        } else if (reviewCount >= ACTIVE_THRESHOLD) {
            return MemberGrade.ACTIVE;
        } else {
            return MemberGrade.NEWCOMER;
        }
    }

    /**
     * 특정 회원들의 등급을 벌크 업데이트
     */
    private int updateMemberGrades(List<Long> memberIds, MemberGrade grade) {
        if (memberIds.isEmpty()) {
            return 0;
        }

        String jpql = "UPDATE Member m SET m.memberGrade = :grade WHERE m.id IN :memberIds";

        return entityManager.createQuery(jpql)
                .setParameter("grade", grade)
                .setParameter("memberIds", memberIds)
                .executeUpdate();
    }

    /**
     * 리뷰 개수로 등급을 결정하는 유틸리티 메서드 (public)
     * - 외부에서 특정 회원의 등급을 계산할 때 사용 가능
     */
    public MemberGrade calculateGrade(int reviewCount) {
        return determineGrade(reviewCount);
    }
}