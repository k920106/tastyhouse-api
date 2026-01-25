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
@Table(name = "PRODUCT_COMMON_OPTION_GROUP")
public class ProductCommonOptionGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired;

    @Column(name = "is_multiple_select", nullable = false)
    private Boolean isMultipleSelect;

    @Column(name = "min_select")
    private Integer minSelect;

    @Column(name = "max_select")
    private Integer maxSelect;

    @Column(name = "sort", nullable = false)
    private Integer sort;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Builder
    public ProductCommonOptionGroup(Long productId, String name, String description, Boolean isRequired,
                                    Boolean isMultipleSelect, Integer minSelect, Integer maxSelect,
                                    Integer sort, Boolean isActive) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.isRequired = isRequired != null ? isRequired : false;
        this.isMultipleSelect = isMultipleSelect != null ? isMultipleSelect : false;
        this.minSelect = minSelect;
        this.maxSelect = maxSelect;
        this.sort = sort;
        this.isActive = isActive != null ? isActive : true;
    }

    public void update(String name, String description, Boolean isRequired, Boolean isMultipleSelect,
                       Integer minSelect, Integer maxSelect, Integer sort, Boolean isActive) {
        this.name = name;
        this.description = description;
        this.isRequired = isRequired;
        this.isMultipleSelect = isMultipleSelect;
        this.minSelect = minSelect;
        this.maxSelect = maxSelect;
        this.sort = sort;
        this.isActive = isActive;
    }
}
