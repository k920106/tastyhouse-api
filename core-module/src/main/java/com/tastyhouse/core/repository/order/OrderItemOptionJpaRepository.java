package com.tastyhouse.core.repository.order;

import com.tastyhouse.core.entity.order.OrderItemOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemOptionJpaRepository extends JpaRepository<OrderItemOption, Long> {

    List<OrderItemOption> findByOrderItemId(Long orderItemId);
}
