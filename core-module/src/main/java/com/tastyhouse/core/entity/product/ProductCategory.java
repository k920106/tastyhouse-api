package com.tastyhouse.core.entity.product;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "PRODUCT_CATEGORY")
public class ProductCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "sort", nullable = false)
    private Integer sort;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Builder
    public ProductCategory(Long placeId, String name, Integer sort, Boolean isActive) {
        this.placeId = placeId;
        this.name = name;
        this.sort = sort;
        this.isActive = isActive != null ? isActive : true;
    }

    public void update(String displayName, Integer sort, Boolean isActive) {
        this.name = displayName;
        this.sort = sort;
        this.isActive = isActive;
    }
}
