package com.tastyhouse.webapi.member;

import com.tastyhouse.core.repository.member.MemberJpaRepository;
import com.tastyhouse.core.repository.point.MemberPointJpaRepository;
import com.tastyhouse.webapi.member.response.MemberContactResponse;
import com.tastyhouse.webapi.member.response.MemberInfoResponse;
import com.tastyhouse.webapi.member.response.PointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberPointJpaRepository memberPointJpaRepository;

    public Optional<MemberInfoResponse> findMemberInfo(Long memberId) {
        return memberJpaRepository.findById(memberId)
            .map(member -> new MemberInfoResponse(
                member.getId(),
                member.getProfileImageUrl()
            ));
    }

    public PointResponse getMemberPoint(Long memberId) {
        return memberPointJpaRepository.findByMemberId(memberId)
            .map(PointResponse::from)
            .orElseGet(() -> PointResponse.builder()
                .availablePoints(0)
                .expiredThisMonth(0)
                .build());
    }

    public Optional<MemberContactResponse> getMemberContact(Long memberId) {
        return memberJpaRepository.findById(memberId)
            .map(member -> new MemberContactResponse(
                member.getFullName(),
                member.getPhoneNumber(),
                member.getUsername()
            ));
    }
}
