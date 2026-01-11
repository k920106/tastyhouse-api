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
@Table(name = "PLACE_PHOTO_CATEGORY")
public class PlacePhotoCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name; // 카테고리명 (예: "가게 외관")

    @Builder
    public PlacePhotoCategory(String name) {
        this.name = name;
    }

    public void update(String name) {
        this.name = name;
    }
}
