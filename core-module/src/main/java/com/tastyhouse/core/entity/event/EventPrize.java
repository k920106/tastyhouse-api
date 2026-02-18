package com.tastyhouse.core.entity.event;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "EVENT_PRIZE",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_event_prize_rank",
            columnNames = {"event_id", "prize_rank"}
        )
    },
    indexes = {
        @Index(name = "idx_event_prize", columnList = "event_id, prize_rank"),
        @Index(name = "idx_prize_brand", columnList = "brand"),
        @Index(name = "idx_prize_name", columnList = "name")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventPrize extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "prize_rank", nullable = false)
    private Integer prizeRank;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "brand", nullable = false, length = 100)
    private String brand;

    @Column(name = "image_file_id")
    private Long imageFileId;

    @Builder
    public EventPrize(
        Long eventId,
        Integer prizeRank,
        String name,
        String brand,
        Long imageFileId
    ) {
        this.eventId = eventId;
        this.prizeRank = prizeRank;
        this.name = name;
        this.brand = brand;
        this.imageFileId = imageFileId;
    }
}
