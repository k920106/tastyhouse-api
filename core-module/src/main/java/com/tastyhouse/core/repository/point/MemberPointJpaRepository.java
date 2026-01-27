package com.tastyhouse.core.repository.point;

import com.tastyhouse.core.entity.point.MemberPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberPointJpaRepository extends JpaRepository<MemberPoint, Long> {
    Optional<MemberPoint> findByMemberId(Long memberId);
}
