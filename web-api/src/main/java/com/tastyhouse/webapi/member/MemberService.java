package com.tastyhouse.webapi.member;

import com.tastyhouse.core.entity.place.dto.MyBookmarkedPlaceItemDto;
import com.tastyhouse.core.entity.rank.MemberReviewRank;
import com.tastyhouse.core.entity.rank.RankType;
import com.tastyhouse.core.entity.review.dto.MyReviewListItemDto;
import com.tastyhouse.core.repository.member.MemberJpaRepository;
import com.tastyhouse.core.repository.place.PlaceRepository;
import com.tastyhouse.core.repository.point.MemberPointHistoryJpaRepository;
import com.tastyhouse.core.repository.point.MemberPointJpaRepository;
import com.tastyhouse.core.repository.rank.MemberReviewRankJpaRepository;
import com.tastyhouse.core.repository.review.ReviewRepository;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.coupon.CouponService;
import com.tastyhouse.webapi.coupon.response.MemberCouponListItemResponse;
import com.tastyhouse.webapi.file.FileService;
import com.tastyhouse.webapi.member.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final FileService fileService;

    private final MemberJpaRepository memberJpaRepository;
    private final MemberPointJpaRepository memberPointJpaRepository;
    private final MemberPointHistoryJpaRepository memberPointHistoryJpaRepository;
    private final MemberReviewRankJpaRepository memberReviewRankJpaRepository;
    private final CouponService couponService;
    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;

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
        // 전체 리뷰 개수 조회 (ALL 타입, 가장 최근 데이터)
        Integer reviewCount = memberReviewRankJpaRepository
            .findLatestByMemberIdAndRankType(memberId, RankType.ALL)
            .map(MemberReviewRank::getReviewCount)
            .orElse(0);

        return MyReviewStatsResponse.builder()
            .totalReviewCount(reviewCount)
            .build();
    }
}
