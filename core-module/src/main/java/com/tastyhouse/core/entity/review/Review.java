package com.tastyhouse.core.entity.review;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "REVIEW")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content; // 내용

    @Column(name = "total_rating", nullable = false)
    private Double totalRating; // 총 평점

    @Column(name = "taste_rating")
    private Double tasteRating; // 맛

    @Column(name = "amount_rating")
    private Double amountRating; // 양

    @Column(name = "price_rating")
    private Double priceRating; // 가격

    @Column(name = "atmosphere_rating")
    private Double atmosphereRating; // 분위기

    @Column(name = "kindness_rating")
    private Double kindnessRating; // 친절도

    @Column(name = "hygiene_rating")
    private Double hygieneRating; // 위생

    @Column(name = "will_revisit")
    private Boolean willRevisit; // 재방문 의사

    @Column(name = "order_id")
    private Long orderId; // null이면 일반 리뷰, 값이 있으면 주문/결제 기반 인증 리뷰

    @Column(name = "is_hidden", nullable = false)
    private Boolean isHidden = false; // 관리자 미노출 여부
}
