package com.tastyhouse.core.repository.payment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.order.QOrder;
import com.tastyhouse.core.entity.order.QOrderItem;
import com.tastyhouse.core.entity.payment.Payment;
import com.tastyhouse.core.entity.payment.PaymentRefund;
import com.tastyhouse.core.entity.payment.PaymentStatus;
import com.tastyhouse.core.entity.payment.QPayment;
import com.tastyhouse.core.entity.payment.QPaymentRefund;
import com.tastyhouse.core.entity.payment.RefundStatus;
import com.tastyhouse.core.entity.payment.dto.OrderListItemDto;
import com.tastyhouse.core.entity.payment.dto.QOrderListItemDto;
import com.tastyhouse.core.entity.place.QPlace;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Payment> findByOrderId(Long orderId) {
        QPayment payment = QPayment.payment;

        Payment result = queryFactory
            .selectFrom(payment)
            .where(payment.orderId.eq(orderId))
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Payment> findByPgTid(String pgTid) {
        QPayment payment = QPayment.payment;

        Payment result = queryFactory
            .selectFrom(payment)
            .where(payment.pgTid.eq(pgTid))
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Payment> findByPgOrderId(String pgOrderId) {
        QPayment payment = QPayment.payment;

        Payment result = queryFactory
            .selectFrom(payment)
            .where(payment.pgOrderId.eq(pgOrderId))
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<Payment> findByPaymentStatusOrderByCreatedAtDesc(PaymentStatus paymentStatus) {
        QPayment payment = QPayment.payment;

        return queryFactory
            .selectFrom(payment)
            .where(payment.paymentStatus.eq(paymentStatus))
            .orderBy(payment.createdAt.desc())
            .fetch();
    }

    @Override
    public boolean existsByOrderId(Long orderId) {
        QPayment payment = QPayment.payment;

        Long count = queryFactory
            .select(payment.count())
            .from(payment)
            .where(payment.orderId.eq(orderId))
            .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public List<PaymentRefund> findRefundsByPaymentIdOrderByCreatedAtDesc(Long paymentId) {
        QPaymentRefund paymentRefund = QPaymentRefund.paymentRefund;

        return queryFactory
            .selectFrom(paymentRefund)
            .where(paymentRefund.paymentId.eq(paymentId))
            .orderBy(paymentRefund.createdAt.desc())
            .fetch();
    }

    @Override
    public Optional<PaymentRefund> findRefundByPgRefundId(String pgRefundId) {
        QPaymentRefund paymentRefund = QPaymentRefund.paymentRefund;

        PaymentRefund result = queryFactory
            .selectFrom(paymentRefund)
            .where(paymentRefund.pgRefundId.eq(pgRefundId))
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<PaymentRefund> findRefundsByRefundStatusOrderByCreatedAtDesc(RefundStatus refundStatus) {
        QPaymentRefund paymentRefund = QPaymentRefund.paymentRefund;

        return queryFactory
            .selectFrom(paymentRefund)
            .where(paymentRefund.refundStatus.eq(refundStatus))
            .orderBy(paymentRefund.createdAt.desc())
            .fetch();
    }

    @Override
    public Page<OrderListItemDto> findOrderListByMemberId(Long memberId, Pageable pageable) {
        QOrder order = QOrder.order;
        QPayment payment = QPayment.payment;
        QPlace place = QPlace.place;
        QOrderItem orderItem = QOrderItem.orderItem;

        // 첫 번째 주문 상품명 서브쿼리
        QOrderItem subOrderItem = new QOrderItem("subOrderItem");

        List<OrderListItemDto> content = queryFactory
            .select(new QOrderListItemDto(
                order.id,
                place.name,
                place.thumbnailImageUrl,
                queryFactory
                    .select(subOrderItem.productName)
                    .from(subOrderItem)
                    .where(subOrderItem.orderId.eq(order.id))
                    .orderBy(subOrderItem.id.asc())
                    .limit(1),
                queryFactory
                    .select(orderItem.quantity.sum())
                    .from(orderItem)
                    .where(orderItem.orderId.eq(order.id)),
                payment.amount,
                payment.paymentStatus,
                payment.approvedAt
            ))
            .from(order)
            .innerJoin(payment).on(payment.orderId.eq(order.id))
            .innerJoin(place).on(place.id.eq(order.placeId))
            .where(order.memberId.eq(memberId))
            .orderBy(order.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .select(order.count())
            .from(order)
            .innerJoin(payment).on(payment.orderId.eq(order.id))
            .where(order.memberId.eq(memberId))
            .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
