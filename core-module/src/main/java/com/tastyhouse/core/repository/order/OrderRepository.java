package com.tastyhouse.core.repository.order;

import com.tastyhouse.core.entity.order.Order;
import com.tastyhouse.core.entity.payment.dto.OrderListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepository {

    Page<Order> findCompletedOrCancelledOrdersByMemberId(Long memberId, Pageable pageable);

    Page<OrderListItemDto> findOrderListByMemberId(Long memberId, Pageable pageable);
}
