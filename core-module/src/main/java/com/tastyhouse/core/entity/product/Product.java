package com.tastyhouse.core.entity.product;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "PRODUCT")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "price", nullable = false)
    private Integer originalPrice;

    @Column(name = "discount_price")
    private Integer discountPrice;

    @Column(name = "discount_rate")
    private BigDecimal discountRate;
}
