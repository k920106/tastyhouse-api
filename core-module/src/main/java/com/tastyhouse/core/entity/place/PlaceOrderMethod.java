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
@Table(name = "PLACE_ORDER_METHOD", uniqueConstraints = {@UniqueConstraint(columnNames = {"place_id", "order_method"})})
public class PlaceOrderMethod extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_method", nullable = false, length = 50, columnDefinition = "VARCHAR(50)")
    private OrderMethod orderMethod;

    @Builder
    public PlaceOrderMethod(Long placeId, OrderMethod orderMethod) {
        this.placeId = placeId;
        this.orderMethod = orderMethod;
    }
}
