package com.tastyhouse.core.entity.place;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "PLACE_AMENITY", uniqueConstraints = {@UniqueConstraint(columnNames = {"place_id", "place_amenity_category_id"})})
public class PlaceAmenity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Column(name = "place_amenity_category_id", nullable = false)
    private Long placeAmenityCategoryId;

    @Builder
    public PlaceAmenity(Long placeId, Long placeAmenityCategoryId) {
        this.placeId = placeId;
        this.placeAmenityCategoryId = placeAmenityCategoryId;
    }
}
