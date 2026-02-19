package com.tastyhouse.core.repository.member;

import com.tastyhouse.core.entity.user.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MemberRepository {

    Optional<Member> findByUsername(String username);

    Page<Member> findAllMembers(Pageable pageable);
}
