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

    @Enumerated(EnumType.STRING)
    @Column(name = "category_type", nullable = false, length = 50, columnDefinition = "VARCHAR(50)")
    private ProductCategoryType categoryType;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "sort", nullable = false)
    private Integer sort;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Builder
    public ProductCategory(Long placeId, ProductCategoryType categoryType, String displayName, Integer sort, Boolean isActive) {
        this.placeId = placeId;
        this.categoryType = categoryType;
        this.displayName = displayName;
        this.sort = sort;
        this.isActive = isActive != null ? isActive : true;
    }

    public void update(ProductCategoryType categoryType, String displayName, Integer sort, Boolean isActive) {
        this.categoryType = categoryType;
        this.displayName = displayName;
        this.sort = sort;
        this.isActive = isActive;
    }
}
