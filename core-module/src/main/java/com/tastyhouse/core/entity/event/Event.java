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
    name = "EVENT",
    indexes = {
        @Index(name = "idx_event_status", columnList = "status"),
        @Index(name = "idx_event_period", columnList = "start_at, end_at")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "subtitle", length = 200)
    private String subtitle;

    @Column(name = "thumbnail_image_url", length = 500)
    private String thumbnailImageUrl;

    @Column(name = "banner_image_url", length = 500)
    private String bannerImageUrl;

    @Column(name = "content_html", columnDefinition = "TEXT")
    private String contentHtml;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'RANKING'")
    private EventType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private EventStatus status;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Builder
    public Event(
        String name,
        String description,
        String subtitle,
        String thumbnailImageUrl,
        String bannerImageUrl,
        String contentHtml,
        EventType type,
        EventStatus status,
        LocalDateTime startAt,
        LocalDateTime endAt
    ) {
        this.name = name;
        this.description = description;
        this.subtitle = subtitle;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.bannerImageUrl = bannerImageUrl;
        this.contentHtml = contentHtml;
        this.type = type;
        this.status = status;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public void updateStatus(EventStatus status) {
        this.status = status;
    }
}
