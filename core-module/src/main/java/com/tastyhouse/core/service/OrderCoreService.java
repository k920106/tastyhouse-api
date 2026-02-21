package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.order.Order;
import com.tastyhouse.core.entity.order.OrderItem;
import com.tastyhouse.core.entity.order.OrderItemOption;
import com.tastyhouse.core.entity.payment.Payment;
import com.tastyhouse.core.entity.payment.dto.OrderListItemDto;
import com.tastyhouse.core.repository.order.OrderItemJpaRepository;
import com.tastyhouse.core.repository.order.OrderItemOptionJpaRepository;
import com.tastyhouse.core.repository.order.OrderJpaRepository;
import com.tastyhouse.core.repository.payment.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderCoreService {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final OrderItemOptionJpaRepository orderItemOptionJpaRepository;
    private final PaymentJpaRepository paymentJpaRepository;

    public Optional<Order> findOrderById(Long orderId) {
        return orderJpaRepository.findById(orderId);
    }

    public Page<OrderListItemDto> findOrderListByMemberId(Long memberId, Pageable pageable) {
        return orderJpaRepository.findOrderListByMemberId(memberId, pageable);
    }

    public List<OrderItem> findOrderItemsByOrderId(Long orderId) {
        return orderItemJpaRepository.findByOrderId(orderId);
    }

    public List<OrderItemOption> findOrderItemOptionsByOrderItemId(Long orderItemId) {
        return orderItemOptionJpaRepository.findByOrderItemId(orderItemId);
    }

    public Optional<Payment> findPaymentByOrderId(Long orderId) {
        return paymentJpaRepository.findByOrderId(orderId);
    }

    @Transactional
    public Order saveOrder(Order order) {
        return orderJpaRepository.save(order);
    }

    @Transactional
    public OrderItem saveOrderItem(OrderItem orderItem) {
        return orderItemJpaRepository.save(orderItem);
    }

    @Transactional
    public void saveOrderItemOption(OrderItemOption orderItemOption) {
        orderItemOptionJpaRepository.save(orderItemOption);
    }
}
