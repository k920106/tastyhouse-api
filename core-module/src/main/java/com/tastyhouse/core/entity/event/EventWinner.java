package com.tastyhouse.core.entity.event;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
    name = "EVENT_WINNER",
    indexes = {
        @Index(name = "idx_event_winner_event_id", columnList = "event_id"),
        @Index(name = "idx_event_winner_announced_at", columnList = "announced_at")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventWinner extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "rank_no", nullable = false)
    private Integer rankNo;

    @Column(name = "winner_name", nullable = false, length = 50)
    private String winnerName;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "announced_at", nullable = false)
    private LocalDateTime announcedAt;

    @Builder
    public EventWinner(
        Long eventId,
        Integer rankNo,
        String winnerName,
        String phoneNumber,
        LocalDateTime announcedAt
    ) {
        this.eventId = eventId;
        this.rankNo = rankNo;
        this.winnerName = winnerName;
        this.phoneNumber = phoneNumber;
        this.announcedAt = announcedAt;
    }

    public String getMaskedPhoneNumber() {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return phoneNumber;
        }

        // 010-1234-5678 -> 010-****-5678 형식으로 마스킹
        String[] parts = phoneNumber.split("-");
        if (parts.length == 3) {
            return parts[0] + "-****-" + parts[2];
        }

        // 하이픈 없는 경우 중간 4자리 마스킹
        int length = phoneNumber.length();
        if (length >= 8) {
            return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
        }

        return phoneNumber;
    }

    public String getMaskedName() {
        if (winnerName == null || winnerName.isEmpty()) {
            return winnerName;
        }

        // 홍길동 -> 홍*동 형식으로 마스킹
        if (winnerName.length() == 2) {
            return winnerName.charAt(0) + "*";
        } else if (winnerName.length() >= 3) {
            return winnerName.charAt(0) + "*" + winnerName.charAt(winnerName.length() - 1);
        }

        return winnerName;
    }
}
