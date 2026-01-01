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
@Table(name = "PRODUCT_OPTION")
public class ProductOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_group_id", nullable = false)
    private Long optionGroupId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "additional_price", nullable = false)
    private Integer additionalPrice;

    @Column(name = "sort", nullable = false)
    private Integer sort;

    @Column(name = "is_sold_out", nullable = false)
    private Boolean isSoldOut;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Builder
    public ProductOption(Long optionGroupId, String name, Integer additionalPrice,
                         Integer sort, Boolean isSoldOut, Boolean isActive) {
        this.optionGroupId = optionGroupId;
        this.name = name;
        this.additionalPrice = additionalPrice != null ? additionalPrice : 0;
        this.sort = sort;
        this.isSoldOut = isSoldOut != null ? isSoldOut : false;
        this.isActive = isActive != null ? isActive : true;
    }

    public void update(String name, Integer additionalPrice, Integer sort,
                       Boolean isSoldOut, Boolean isActive) {
        this.name = name;
        this.additionalPrice = additionalPrice;
        this.sort = sort;
        this.isSoldOut = isSoldOut;
        this.isActive = isActive;
    }

    public void markAsSoldOut() {
        this.isSoldOut = true;
    }

    public void markAsAvailable() {
        this.isSoldOut = false;
    }
}
