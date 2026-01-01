package com.tastyhouse.core.entity.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "PRODUCT_COMMON_OPTION_GROUP",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_product_common_option_group",
                columnNames = {"product_id", "common_option_group_id"}
        ))
public class ProductCommonOptionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "common_option_group_id", nullable = false)
    private Long commonOptionGroupId;

    @Column(name = "sort", nullable = false)
    private Integer sort;

    @Builder
    public ProductCommonOptionGroup(Long productId, Long commonOptionGroupId, Integer sort) {
        this.productId = productId;
        this.commonOptionGroupId = commonOptionGroupId;
        this.sort = sort;
    }

    public void updateSort(Integer sort) {
        this.sort = sort;
    }
}
