package com.tastyhouse.core.entity.product;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PRODUCT")
@Getter
@Setter
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId; // 매체사 (PK)

    @Column(name = "product_code", nullable = false, unique = true)
    private String productCode; // 상품 코드 (G123456789)

    @Column(name = "name", nullable = false)
    private String name; // 상품명

    @Column(name = "brand_id", nullable = false)
    private Long brandId; // 교환처 (PK)

    @Column(name = "supply_id", nullable = false)
    private Long supplyId; // 공급처 (PK)

    @Column(name = "validity_period", nullable = false)
    private int validityPeriod; // 유효일수

    @Column(name = "exhibition_price", nullable = false)
    private int exhibitionPrice; // 전시가

    @Column(name = "regular_price", nullable = false)
    private int regularPrice; // 정상가

    @Column(name = "supply_price", nullable = false)
    private int supplyPrice; // 공급가

    @Column(name = "display", nullable = false)
    private Boolean isDisplay; // 전시상태

    @Column(name = "sort", nullable = false)
    private Integer sort; // 우선순위
}
