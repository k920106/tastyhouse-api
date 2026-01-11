package com.tastyhouse.core.entity.place;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "PLACE_BANNER_IMAGE")
public class PlaceBannerImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "sort")
    private Integer sort; // 정렬 순서
}