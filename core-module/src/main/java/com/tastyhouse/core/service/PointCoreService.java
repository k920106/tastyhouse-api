package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.point.MemberPoint;
import com.tastyhouse.core.entity.point.MemberPointHistory;
import com.tastyhouse.core.entity.point.PointType;
import com.tastyhouse.core.repository.point.MemberPointHistoryJpaRepository;
import com.tastyhouse.core.repository.point.MemberPointJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointCoreService {

    private final MemberPointJpaRepository memberPointJpaRepository;
    private final MemberPointHistoryJpaRepository memberPointHistoryJpaRepository;

    public Optional<MemberPoint> findMemberPoint(Long memberId) {
        return memberPointJpaRepository.findByMemberId(memberId);
    }

    @Transactional
    public void usePoints(Long memberId, int pointAmount) {
        MemberPoint memberPoint = memberPointJpaRepository.findByMemberId(memberId)
            .orElseThrow(() -> new IllegalArgumentException("포인트 정보를 찾을 수 없습니다."));

        if (memberPoint.getAvailablePoints() < pointAmount) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }

        memberPoint.deductPoints(pointAmount);

        memberPointHistoryJpaRepository.save(
            MemberPointHistory.builder()
                .memberId(memberId)
                .pointType(PointType.USE)
                .pointAmount(-pointAmount)
                .reason("주문 결제 사용")
                .build()
        );
    }

    @Transactional
    public void earnPoints(Long memberId, int pointAmount, String reason) {
        MemberPoint memberPoint = memberPointJpaRepository.findByMemberId(memberId)
            .orElseThrow(() -> new IllegalArgumentException("포인트 정보를 찾을 수 없습니다."));

        memberPoint.addPoints(pointAmount);

        memberPointHistoryJpaRepository.save(
            MemberPointHistory.builder()
                .memberId(memberId)
                .pointType(PointType.EARNED)
                .pointAmount(pointAmount)
                .reason(reason)
                .build()
        );
    }
}
