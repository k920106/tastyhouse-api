package com.tastyhouse.core.entity.place;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "PLACE_FOOD_TYPE", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"place_id", "food_type"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceFoodType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "food_type", nullable = false, columnDefinition = "VARCHAR(50)")
    private FoodType foodType;

    @Builder
    public PlaceFoodType(Long placeId, FoodType foodType) {
        this.placeId = placeId;
        this.foodType = foodType;
    }
}
