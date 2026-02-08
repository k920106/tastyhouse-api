package com.tastyhouse.core.repository.payment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.payment.PaymentStatus;
import com.tastyhouse.core.entity.payment.dto.MyPaymentListItemDto;
import com.tastyhouse.core.entity.payment.dto.QMyPaymentListItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tastyhouse.core.entity.order.QOrder.order;
import static com.tastyhouse.core.entity.order.QOrderItem.orderItem;
import static com.tastyhouse.core.entity.payment.QPayment.payment;
import static com.tastyhouse.core.entity.place.QPlace.place;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MyPaymentListItemDto> findMyPayments(Long memberId, Pageable pageable) {
        List<MyPaymentListItemDto> content = queryFactory
            .select(new QMyPaymentListItemDto(
                payment.id,
                place.name,
                place.thumbnailImageUrl,
                orderItem.productName.min(),
                orderItem.count().intValue(),
                payment.amount,
                payment.paymentStatus,
                payment.createdAt
            ))
            .from(payment)
            .join(order).on(payment.orderId.eq(order.id))
            .join(place).on(order.placeId.eq(place.id))
            .leftJoin(orderItem).on(order.id.eq(orderItem.orderId))
            .where(order.memberId.eq(memberId)
                .and(payment.paymentStatus.in(PaymentStatus.COMPLETED, PaymentStatus.CANCELLED)))
            .groupBy(payment.id, place.name, place.thumbnailImageUrl, payment.amount, payment.paymentStatus, payment.createdAt)
            .orderBy(payment.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(payment.count())
            .from(payment)
            .join(order).on(payment.orderId.eq(order.id))
            .where(order.memberId.eq(memberId)
                .and(payment.paymentStatus.in(PaymentStatus.COMPLETED, PaymentStatus.CANCELLED)))
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}
