package com.tastyhouse.core.repository.member;

import com.tastyhouse.core.entity.user.MemberWithdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberWithdrawalJpaRepository extends JpaRepository<MemberWithdrawal, Long> {
}
