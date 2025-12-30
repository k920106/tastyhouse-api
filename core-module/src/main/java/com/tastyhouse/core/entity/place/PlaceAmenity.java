package com.tastyhouse.core.entity.place;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "PLACE_AMENITY", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"place_id", "amenity"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceAmenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "amenity", nullable = false, columnDefinition = "VARCHAR(50)")
    private Amenity amenity;

    @Builder
    public PlaceAmenity(Long placeId, Amenity amenity) {
        this.placeId = placeId;
        this.amenity = amenity;
    }
}
