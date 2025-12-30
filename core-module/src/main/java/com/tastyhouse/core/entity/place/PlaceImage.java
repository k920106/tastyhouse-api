package com.tastyhouse.core.entity.place;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "PLACE_IMAGE")
public class PlaceImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "is_thumbnail")
    private Boolean isThumbnail;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_category", length = 20, columnDefinition = "VARCHAR(20)")
    private PlaceImageCategory imageCategory; // 이미지 카테고리 (외관, 내부, 음식 등)

    @Column(name = "sort")
    private Integer sort; // 정렬 순서
}