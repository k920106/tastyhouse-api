package com.tastyhouse.core.repository.order;

import com.tastyhouse.core.entity.order.Order;
import com.tastyhouse.core.entity.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    Page<Order> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    List<Order> findByMemberIdAndOrderStatusOrderByCreatedAtDesc(Long memberId, OrderStatus orderStatus);

    List<Order> findByPlaceIdOrderByCreatedAtDesc(Long placeId);

    List<Order> findByPlaceIdAndOrderStatusOrderByCreatedAtDesc(Long placeId, OrderStatus orderStatus);

    boolean existsByOrderNumber(String orderNumber);
}
