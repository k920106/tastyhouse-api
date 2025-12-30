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
@Table(name = "PLACE_FOOD_TYPE", uniqueConstraints = {@UniqueConstraint(columnNames = {"place_id", "place_food_type_category_id"})})
public class PlaceFoodType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Column(name = "place_food_type_category_id", nullable = false)
    private Long placeFoodTypeCategoryId;

    @Builder
    public PlaceFoodType(Long placeId, Long placeFoodTypeCategoryId) {
        this.placeId = placeId;
        this.placeFoodTypeCategoryId = placeFoodTypeCategoryId;
    }
}
