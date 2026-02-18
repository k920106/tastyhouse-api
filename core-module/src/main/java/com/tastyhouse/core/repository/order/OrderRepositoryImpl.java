package com.tastyhouse.core.repository.order;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.order.Order;
import com.tastyhouse.core.entity.order.QOrder;
import com.tastyhouse.core.entity.payment.PaymentStatus;
import com.tastyhouse.core.entity.payment.QPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> findCompletedOrCancelledOrdersByMemberId(Long memberId, Pageable pageable) {
        QOrder order = QOrder.order;
        QPayment payment = QPayment.payment;

        List<Order> content = queryFactory
            .selectFrom(order)
            .innerJoin(payment).on(
                payment.orderId.eq(order.id)
                    .and(payment.paymentStatus.in(PaymentStatus.COMPLETED, PaymentStatus.CANCELLED))
            )
            .where(order.memberId.eq(memberId))
            .orderBy(order.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .select(order.count())
            .from(order)
            .innerJoin(payment).on(
                payment.orderId.eq(order.id)
                    .and(payment.paymentStatus.in(PaymentStatus.COMPLETED, PaymentStatus.CANCELLED))
            )
            .where(order.memberId.eq(memberId))
            .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
