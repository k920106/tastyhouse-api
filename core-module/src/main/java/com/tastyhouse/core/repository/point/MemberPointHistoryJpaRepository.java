package com.tastyhouse.core.repository.point;

import com.tastyhouse.core.entity.point.MemberPointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberPointHistoryJpaRepository extends JpaRepository<MemberPointHistory, Long> {
    List<MemberPointHistory> findByMemberIdOrderByCreatedAtDesc(Long memberId);
}
