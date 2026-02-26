package com.tastyhouse.webapi.member;

import com.tastyhouse.core.entity.place.dto.MyBookmarkedPlaceItemDto;
import com.tastyhouse.core.entity.rank.MemberReviewRank;
import com.tastyhouse.core.entity.rank.RankType;
import com.tastyhouse.core.entity.review.dto.MyReviewListItemDto;
import com.tastyhouse.core.entity.user.Member;
import com.tastyhouse.core.entity.user.MemberStatus;
import com.tastyhouse.core.entity.user.MemberWithdrawal;
import com.tastyhouse.core.entity.user.WithdrawalReason;
import com.tastyhouse.core.exception.BusinessException;
import com.tastyhouse.core.exception.EntityNotFoundException;
import com.tastyhouse.core.exception.ErrorCode;
import com.tastyhouse.core.repository.member.MemberJpaRepository;
import com.tastyhouse.core.repository.member.MemberWithdrawalJpaRepository;
import com.tastyhouse.core.repository.place.PlaceRepository;
import com.tastyhouse.core.repository.point.MemberPointHistoryJpaRepository;
import com.tastyhouse.core.repository.point.MemberPointJpaRepository;
import com.tastyhouse.core.repository.rank.MemberReviewRankJpaRepository;
import com.tastyhouse.core.repository.review.ReviewRepository;
import com.tastyhouse.core.common.PageResult;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.config.jwt.JwtTokenProvider;
import com.tastyhouse.webapi.config.jwt.TokenBlacklist;
import com.tastyhouse.webapi.coupon.CouponService;
import com.tastyhouse.webapi.coupon.response.MemberCouponListItemResponse;
import com.tastyhouse.webapi.exception.UnauthorizedException;
import com.tastyhouse.file.FileService;
import com.tastyhouse.webapi.member.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklist tokenBlacklist;

    private final MemberJpaRepository memberJpaRepository;
    private final MemberWithdrawalJpaRepository memberWithdrawalJpaRepository;
    private final MemberPointJpaRepository memberPointJpaRepository;
    private final MemberPointHistoryJpaRepository memberPointHistoryJpaRepository;
    private final MemberReviewRankJpaRepository memberReviewRankJpaRepository;
    private final CouponService couponService;
    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;

    public void verifyPersonalInfoToken(Long memberId, String verifyToken) {
        if (!jwtTokenProvider.validateVerifyToken(verifyToken)) {
            throw new BusinessException(ErrorCode.MEMBER_INFO_AUTH_EXPIRED);
        }

        Long verifiedMemberId = jwtTokenProvider.getMemberIdFromVerifyToken(verifyToken);
        if (!verifiedMemberId.equals(memberId)) {
            throw new UnauthorizedException("인증 정보가 일치하지 않습니다.");
        }
    }

    public void verifyPhoneToken(Long memberId, String phoneVerifyToken, String phoneNumber) {
        if (!StringUtils.hasText(phoneVerifyToken)) {
            throw new BusinessException(ErrorCode.MEMBER_PHONE_SMS_REQUIRED);
        }

        if (!jwtTokenProvider.validatePhoneVerifyToken(phoneVerifyToken)) {
            throw new BusinessException(ErrorCode.MEMBER_PHONE_AUTH_EXPIRED);
        }

        Long phoneVerifiedMemberId = jwtTokenProvider.getMemberIdFromPhoneVerifyToken(phoneVerifyToken);
        if (!phoneVerifiedMemberId.equals(memberId)) {
            throw new UnauthorizedException("휴대폰 인증 정보가 일치하지 않습니다.");
        }

        String verifiedPhoneNumber = jwtTokenProvider.getPhoneNumberFromPhoneVerifyToken(phoneVerifyToken);
        if (!verifiedPhoneNumber.equals(phoneNumber)) {
            throw new BusinessException(ErrorCode.MEMBER_PHONE_MISMATCH);
        }
    }

    public void invalidateToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String accessToken = bearerToken.substring(7).trim();
            if (jwtTokenProvider.validateToken(accessToken)) {
                long expirationMillis = jwtTokenProvider.getExpirationMillis(accessToken);
                tokenBlacklist.add(accessToken, expirationMillis);
            }
        }
    }

    public void verifyPassword(Long memberId, String rawPassword) {
        Member member = memberJpaRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new BusinessException(ErrorCode.MEMBER_PASSWORD_MISMATCH);
        }
    }

    public PersonalInfoResponse getPersonalInfo(Long memberId) {
        Member member = memberJpaRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 회원입니다."));

        return PersonalInfoResponse.from(member);
    }

    @Transactional
    public void withdrawMember(Long memberId, WithdrawalReason reason, String reasonDetail) {
        memberJpaRepository.findById(memberId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 회원입니다."))
            .setMemberStatus(MemberStatus.DELETED);

        memberWithdrawalJpaRepository.save(
            MemberWithdrawal.builder()
                .memberId(memberId)
                .reason(reason)
                .reasonDetail(reasonDetail)
                .build()
        );
    }

    public PointResponse getMemberPoint(Long memberId) {
        return memberPointJpaRepository.findByMemberId(memberId)
            .map(PointResponse::from)
            .orElseGet(() -> PointResponse.builder()
                .availablePoints(0)
                .expiredThisMonth(0)
                .build());
    }

    public PointHistoryResponse getPointHistory(Long memberId) {
        PointResponse pointResponse = getMemberPoint(memberId);

        List<PointHistoryItemResponse> histories = memberPointHistoryJpaRepository
            .findByMemberIdOrderByCreatedAtDesc(memberId)
            .stream()
            .map(PointHistoryItemResponse::from)
            .collect(Collectors.toList());

        return PointHistoryResponse.builder()
            .availablePoints(pointResponse.getAvailablePoints())
            .expiredThisMonth(pointResponse.getExpiredThisMonth())
            .histories(histories)
            .build();
    }

    public List<MemberCouponListItemResponse> getMemberCoupons(Long memberId) {
        return couponService.getMemberCoupons(memberId);
    }

    public List<MemberCouponListItemResponse> getAvailableMemberCoupons(Long memberId) {
        return couponService.getAvailableMemberCoupons(memberId);
    }

    public UsablePointResponse getUsablePoint(Long memberId) {
        return memberPointJpaRepository.findByMemberId(memberId)
            .map(UsablePointResponse::from)
            .orElseGet(() -> UsablePointResponse.builder()
                .usablePoints(0)
                .build());
    }

    public Optional<MemberProfileResponse> getMemberProfile(Long memberId) {
        return memberJpaRepository.findById(memberId)
            .map(member -> {
                String profileImageUrl = null;
                if (member.getProfileImageFileId() != null) {
                    profileImageUrl = fileService.getFileUrl(member.getProfileImageFileId());
                }

                return MemberProfileResponse.builder()
                    .id(member.getId())
                    .nickname(member.getNickname())
                    .grade(member.getMemberGrade())
                    .statusMessage(member.getStatusMessage())
                    .profileImageUrl(profileImageUrl)
                    .fullName(member.getFullName())
                    .phoneNumber(member.getPhoneNumber())
                    .email(member.getUsername())
                    .build();
            });
    }

    @Transactional
    public void updatePassword(Long memberId, String newPassword, String newPasswordConfirm) {
        if (!newPassword.equals(newPasswordConfirm)) {
            throw new BusinessException(ErrorCode.MEMBER_PASSWORD_CONFIRM_MISMATCH);
        }

        Member member = memberJpaRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND, "인증된 회원을 찾을 수 없습니다."));

        if (passwordEncoder.matches(newPassword, member.getPassword())) {
            throw new BusinessException(ErrorCode.MEMBER_PASSWORD_SAME_AS_OLD);
        }

        member.setPassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void updateMemberProfile(Long memberId, String nickname, String statusMessage, Long profileImageFileId) {
        memberJpaRepository.findById(memberId).ifPresent(member -> {
            if (nickname != null) {
                member.setNickname(nickname);
            }
            if (statusMessage != null) {
                member.setStatusMessage(statusMessage);
            }
            if (profileImageFileId != null) {
                member.setProfileImageFileId(profileImageFileId);
            }
        });
    }

    @Transactional
    public void updatePersonalInfo(Long memberId, String fullName, String phoneNumber, Integer birthDate,
                                   com.tastyhouse.core.entity.user.Gender gender,
                                   Boolean pushNotificationEnabled, Boolean marketingInfoEnabled,
                                   Boolean eventInfoEnabled) {
        memberJpaRepository.findById(memberId).ifPresent(member -> {
            if (fullName != null) {
                member.setFullName(fullName);
            }
            if (phoneNumber != null) {
                member.setPhoneNumber(phoneNumber);
            }
            if (birthDate != null) {
                member.setBirthDate(birthDate);
            }
            if (gender != null) {
                member.setGender(gender);
            }
            if (pushNotificationEnabled != null) {
                member.setPushNotificationEnabled(pushNotificationEnabled);
            }
            if (marketingInfoEnabled != null) {
                member.setMarketingInfoEnabled(marketingInfoEnabled);
            }
            if (eventInfoEnabled != null) {
                member.setEventInfoEnabled(eventInfoEnabled);
            }
        });
    }

    public PageResult<MyReviewListItemResponse> getMyReviews(Long memberId, PageRequest pageRequest) {
        org.springframework.data.domain.PageRequest springPageRequest =
            org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize());

        Page<MyReviewListItemDto> page = reviewRepository.findMyReviews(memberId, springPageRequest);

        List<MyReviewListItemResponse> content = page.getContent().stream()
            .map(MyReviewListItemResponse::from)
            .collect(Collectors.toList());

        return new PageResult<>(
            content,
            page.getTotalElements(),
            page.getTotalPages(),
            page.getNumber(),
            page.getSize()
        );
    }

    public PageResult<MyBookmarkedPlaceListItemResponse> getMyBookmarkedPlaces(Long memberId, PageRequest pageRequest) {
        org.springframework.data.domain.PageRequest springPageRequest =
            org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize());

        Page<MyBookmarkedPlaceItemDto> page = placeRepository.findMyBookmarkedPlaces(memberId, springPageRequest);

        List<MyBookmarkedPlaceListItemResponse> content = page.getContent().stream()
            .map(MyBookmarkedPlaceListItemResponse::from)
            .collect(Collectors.toList());

        return new PageResult<>(
            content,
            page.getTotalElements(),
            page.getTotalPages(),
            page.getNumber(),
            page.getSize()
        );
    }

    public MyReviewStatsResponse getMyReviewStats(Long memberId) {
        Integer reviewCount = memberReviewRankJpaRepository
            .findLatestByMemberIdAndRankType(memberId, RankType.ALL)
            .map(MemberReviewRank::getReviewCount)
            .orElse(0);

        return MyReviewStatsResponse.builder()
            .totalReviewCount(reviewCount)
            .build();
    }
}
