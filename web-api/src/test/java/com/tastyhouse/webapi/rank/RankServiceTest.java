package com.tastyhouse.webapi.rank;

import com.tastyhouse.core.entity.rank.RankType;
import com.tastyhouse.core.entity.rank.dto.MemberRankDto;
import com.tastyhouse.core.entity.user.MemberGrade;
import com.tastyhouse.core.service.RankCoreService;
import com.tastyhouse.webapi.rank.response.MyRankResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankServiceTest {

    @Mock
    private RankCoreService rankCoreService;

    @InjectMocks
    private RankService rankService;

    private Long memberId;
    private String rankType;
    private LocalDate baseDate;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        rankType = "ALL";
        baseDate = LocalDate.now();
    }

    @Test
    @DisplayName("내 랭킹 조회 성공")
    void testGetMyMemberRank_success() {
        // Given
        MemberRankDto mockMemberRankDto = new MemberRankDto(
            memberId,
            "테스트유저",
            "http://example.com/profile.jpg",
            100,
            1,
            MemberGrade.NEWCOMER
        );
        when(rankCoreService.getMemberRank(eq(memberId), any(RankType.class), eq(baseDate)))
            .thenReturn(Optional.of(mockMemberRankDto));

        // When
        MyRankResponse result = rankService.getMyMemberRank(memberId, rankType);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo(memberId);
        assertThat(result.getNickname()).isEqualTo("테스트유저");
        assertThat(result.getReviewCount()).isEqualTo(100);
        assertThat(result.getRankNo()).isEqualTo(1);
        assertThat(result.getGrade()).isEqualTo(MemberGrade.NEWCOMER);
    }

    @Test
    @DisplayName("내 랭킹 조회 실패 - 랭킹 정보 없음")
    void testGetMyMemberRank_notFound() {
        // Given
        when(rankCoreService.getMemberRank(eq(memberId), any(RankType.class), eq(baseDate)))
            .thenReturn(Optional.empty());

        // When
        MyRankResponse result = rankService.getMyMemberRank(memberId, rankType);

        // Then
        assertThat(result).isNull();
    }
}
