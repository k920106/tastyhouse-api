package com.tastyhouse.core.entity.product;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "PRODUCT")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Column(name = "product_category_id")
    private Long productCategoryId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "price", nullable = false)
    private Integer originalPrice;

    @Column(name = "discount_price")
    private Integer discountPrice;

    @Column(name = "discount_rate")
    private BigDecimal discountRate;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "review_count")
    private Integer reviewCount;

    @Column(name = "is_representative")
    private Boolean isRepresentative;

    @Column(name = "is_sold_out", nullable = false)
    private Boolean isSoldOut;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "sort", nullable = false)
    private Integer sort;

    @Builder
    public Product(Long placeId, Long productCategoryId, String name, String description,
                   String imageUrl, Integer originalPrice, Integer discountPrice,
                   BigDecimal discountRate, Double rating, Integer reviewCount,
                   Boolean isRepresentative, Boolean isSoldOut, Boolean isActive, Integer sort) {
        this.placeId = placeId;
        this.productCategoryId = productCategoryId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
        this.discountRate = discountRate;
        this.rating = rating;
        this.reviewCount = reviewCount != null ? reviewCount : 0;
        this.isRepresentative = isRepresentative != null ? isRepresentative : false;
        this.isSoldOut = isSoldOut != null ? isSoldOut : false;
        this.isActive = isActive != null ? isActive : true;
        this.sort = sort;
    }

    public void update(Long productCategoryId, String name, String description,
                       String imageUrl, Integer originalPrice, Integer discountPrice,
                       BigDecimal discountRate, Boolean isRepresentative,
                       Boolean isSoldOut, Boolean isActive, Integer sort) {
        this.productCategoryId = productCategoryId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
        this.discountRate = discountRate;
        this.isRepresentative = isRepresentative;
        this.isSoldOut = isSoldOut;
        this.isActive = isActive;
        this.sort = sort;
    }

    public void markAsSoldOut() {
        this.isSoldOut = true;
    }

    public void markAsAvailable() {
        this.isSoldOut = false;
    }
}
