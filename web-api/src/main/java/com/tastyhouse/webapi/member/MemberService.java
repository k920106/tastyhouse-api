package com.tastyhouse.webapi.member;

import com.tastyhouse.core.repository.member.MemberJpaRepository;
import com.tastyhouse.webapi.member.response.MemberInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberJpaRepository memberJpaRepository;

    public Optional<MemberInfoResponse> findMemberInfo(Long memberId) {
        return memberJpaRepository.findById(memberId)
            .map(member -> new MemberInfoResponse(
                member.getId(),
                member.getProfileImageUrl()
            ));
    }
}
