package com.tastyhouse.webapi.payment;

import com.tastyhouse.core.entity.order.Order;
import com.tastyhouse.core.entity.order.OrderStatus;
import com.tastyhouse.core.entity.payment.Payment;
import com.tastyhouse.core.entity.payment.PaymentMethod;
import com.tastyhouse.core.entity.payment.PaymentRefund;
import com.tastyhouse.core.entity.payment.PaymentStatus;
import com.tastyhouse.core.entity.payment.PgProvider;
import com.tastyhouse.core.entity.point.MemberPoint;
import com.tastyhouse.core.entity.point.MemberPointHistory;
import com.tastyhouse.core.entity.point.PointType;
import com.tastyhouse.core.repository.order.OrderJpaRepository;
import com.tastyhouse.core.repository.payment.PaymentJpaRepository;
import com.tastyhouse.core.repository.payment.PaymentRefundJpaRepository;
import com.tastyhouse.core.repository.point.MemberPointHistoryJpaRepository;
import com.tastyhouse.core.repository.point.MemberPointJpaRepository;
import com.tastyhouse.external.payment.toss.TossPaymentClient;
import com.tastyhouse.external.payment.toss.dto.TossPaymentConfirmResult;
import com.tastyhouse.webapi.payment.request.PaymentCancelRequest;
import com.tastyhouse.webapi.payment.request.PaymentConfirmRequest;
import com.tastyhouse.webapi.payment.request.PaymentCreateRequest;
import com.tastyhouse.webapi.payment.request.RefundRequest;
import com.tastyhouse.webapi.payment.request.TossPaymentConfirmApiRequest;
import com.tastyhouse.webapi.payment.response.PaymentRefundResponse;
import com.tastyhouse.webapi.payment.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentRefundJpaRepository paymentRefundJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final MemberPointJpaRepository memberPointJpaRepository;
    private final MemberPointHistoryJpaRepository memberPointHistoryJpaRepository;
    private final TossPaymentClient tossPaymentClient;

    private static final int CASH_POINT_EARN_RATE = 10;

    @Transactional
    public PaymentResponse createPayment(Long memberId, PaymentCreateRequest request) {
        Order order = orderJpaRepository.findById(request.orderId())
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!order.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 주문만 결제할 수 있습니다.");
        }

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("결제할 수 없는 주문 상태입니다.");
        }

        if (paymentJpaRepository.existsByOrderId(request.orderId())) {
            throw new IllegalStateException("이미 결제가 진행 중인 주문입니다.");
        }

        String pgOrderId = generatePgOrderId();

        Payment payment = Payment.builder()
            .orderId(request.orderId())
            .paymentMethod(request.paymentMethod())
            .paymentStatus(PaymentStatus.PENDING)
            .amount(order.getFinalAmount())
            .pgOrderId(pgOrderId)
            .build();

        Payment savedPayment = paymentJpaRepository.save(payment);

        return buildPaymentResponse(savedPayment);
    }

    @Transactional
    public PaymentResponse confirmPayment(PaymentConfirmRequest request) {
        Payment payment = paymentJpaRepository.findById(request.paymentId())
            .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("승인 대기 중인 결제만 처리할 수 있습니다.");
        }

        Order order = orderJpaRepository.findById(payment.getOrderId())
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        payment.updatePgInfo(request.pgProvider(), request.pgTid(), request.pgOrderId());

        if (request.cardCompany() != null) {
            payment.updateCardInfo(request.cardCompany(), request.cardNumber(), request.installmentMonths());
        }

        payment.complete(request.pgTid(), LocalDateTime.now(), request.receiptUrl());

        order.confirm();

        return buildPaymentResponse(payment);
    }

    @Transactional
    public PaymentResponse confirmTossPayment(Long memberId, TossPaymentConfirmApiRequest request) {
        Payment payment = paymentJpaRepository.findByPgOrderId(request.pgOrderId())
            .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

        Order order = orderJpaRepository.findById(payment.getOrderId())
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!order.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 결제만 승인할 수 있습니다.");
        }

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("승인 대기 중인 결제만 처리할 수 있습니다.");
        }

        if (!payment.getAmount().equals(request.amount())) {
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }

        TossPaymentConfirmResult result = tossPaymentClient.confirmPayment(
            request.paymentKey(),
            request.pgOrderId(),
            request.amount()
        );

        if (!result.isSuccess()) {
            log.error("Toss payment confirm failed. orderId: {}, errorCode: {}, errorMessage: {}",
                request.pgOrderId(), result.getErrorCode(), result.getErrorMessage());
            payment.fail();
            throw new IllegalStateException(result.getErrorMessage() != null
                ? result.getErrorMessage()
                : "결제 승인에 실패했습니다.");
        }

        payment.updatePgInfo(PgProvider.TOSS, result.getPaymentKey(), result.getOrderId());

        if (result.getCardCompany() != null) {
            payment.updateCardInfo(
                result.getCardCompany(),
                result.getCardNumber(),
                result.getInstallmentPlanMonths()
            );
        }

        payment.complete(result.getPaymentKey(), result.getApprovedAt(), result.getReceiptUrl());
        order.confirm();

        log.info("Toss payment confirmed successfully. paymentId: {}, orderId: {}, amount: {}",
            payment.getId(), result.getOrderId(), result.getTotalAmount());

        return buildPaymentResponse(payment);
    }

    @Transactional
    public PaymentResponse cancelPayment(Long memberId, Long paymentId, PaymentCancelRequest request) {
        Payment payment = paymentJpaRepository.findById(paymentId)
            .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

        Order order = orderJpaRepository.findById(payment.getOrderId())
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!order.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 결제만 취소할 수 있습니다.");
        }

        if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("완료된 결제만 취소할 수 있습니다.");
        }

        payment.cancel(request.cancelReason());
        order.cancel();

        if (order.getUsedPoint() > 0) {
            MemberPoint memberPoint = memberPointJpaRepository.findByMemberId(memberId).orElse(null);
            if (memberPoint != null) {
                memberPoint.addPoints(order.getUsedPoint());

                MemberPointHistory pointHistory = MemberPointHistory.builder()
                    .memberId(memberId)
                    .pointType(PointType.REFUND)
                    .pointAmount(order.getUsedPoint())
                    .reason("결제 취소 환불")
                    .build();
                memberPointHistoryJpaRepository.save(pointHistory);
            }
        }

        if (order.getEarnedPoint() > 0) {
            MemberPoint memberPoint = memberPointJpaRepository.findByMemberId(memberId).orElse(null);
            if (memberPoint != null && memberPoint.getAvailablePoints() >= order.getEarnedPoint()) {
                memberPoint.deductPoints(order.getEarnedPoint());

                MemberPointHistory pointHistory = MemberPointHistory.builder()
                    .memberId(memberId)
                    .pointType(PointType.USE)
                    .pointAmount(-order.getEarnedPoint())
                    .reason("결제 취소 적립금 회수")
                    .build();
                memberPointHistoryJpaRepository.save(pointHistory);
            }
        }

        return buildPaymentResponse(payment);
    }

    @Transactional
    public PaymentRefundResponse requestRefund(Long memberId, Long paymentId, RefundRequest request) {
        Payment payment = paymentJpaRepository.findById(paymentId)
            .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

        Order order = orderJpaRepository.findById(payment.getOrderId())
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!order.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 결제만 환불할 수 있습니다.");
        }

        if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("완료된 결제만 환불할 수 있습니다.");
        }

        if (request.refundAmount() > payment.getAmount()) {
            throw new IllegalArgumentException("환불 금액이 결제 금액을 초과할 수 없습니다.");
        }

        PaymentRefund refund = PaymentRefund.builder()
            .paymentId(paymentId)
            .refundAmount(request.refundAmount())
            .refundReason(request.refundReason())
            .build();

        PaymentRefund savedRefund = paymentRefundJpaRepository.save(refund);

        return buildRefundResponse(savedRefund);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long memberId, Long orderId) {
        Order order = orderJpaRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!order.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 주문만 조회할 수 있습니다.");
        }

        Payment payment = paymentJpaRepository.findByOrderId(orderId)
            .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        return buildPaymentResponse(payment);
    }

    @Transactional
    public PaymentResponse completeOnSitePayment(Long memberId, Long paymentId) {
        Payment payment = paymentJpaRepository.findById(paymentId)
            .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

        Order order = orderJpaRepository.findById(payment.getOrderId())
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!order.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 결제만 완료 처리할 수 있습니다.");
        }

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("대기 중인 결제만 완료 처리할 수 있습니다.");
        }

        if (!isOnSitePayment(payment.getPaymentMethod())) {
            throw new IllegalStateException("현장결제만 완료 처리할 수 있습니다.");
        }

        processOnSitePaymentCompletion(payment, order, memberId);

        return buildPaymentResponse(payment);
    }

    private void processOnSitePaymentCompletion(Payment payment, Order order, Long memberId) {
        payment.complete(null, LocalDateTime.now(), null);
        order.confirm();

        if (payment.getPaymentMethod() == PaymentMethod.CASH_ON_SITE) {
            int earnedPoint = (int) (payment.getAmount() * CASH_POINT_EARN_RATE / 100.0);

            MemberPoint memberPoint = memberPointJpaRepository.findByMemberId(memberId)
                .orElseGet(() -> {
                    MemberPoint newPoint = MemberPoint.builder()
                        .memberId(memberId)
                        .availablePoints(0)
                        .build();
                    return memberPointJpaRepository.save(newPoint);
                });

            memberPoint.addPoints(earnedPoint);

            MemberPointHistory pointHistory = MemberPointHistory.builder()
                .memberId(memberId)
                .pointType(PointType.EARNED)
                .pointAmount(earnedPoint)
                .reason("현장 현금 결제 적립 (" + CASH_POINT_EARN_RATE + "%)")
                .build();
            memberPointHistoryJpaRepository.save(pointHistory);
        }
    }

    private boolean isOnSitePayment(PaymentMethod method) {
        return method == PaymentMethod.CASH_ON_SITE || method == PaymentMethod.CARD_ON_SITE;
    }

    private String generatePgOrderId() {
        return "PG" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PaymentResponse buildPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
            .id(payment.getId())
            .orderId(payment.getOrderId())
            .paymentMethod(payment.getPaymentMethod())
            .paymentStatus(payment.getPaymentStatus())
            .amount(payment.getAmount())
            .pgProvider(payment.getPgProvider())
            .pgTid(payment.getPgTid())
            .pgOrderId(payment.getPgOrderId())
            .cardCompany(payment.getCardCompany())
            .cardNumber(payment.getCardNumber())
            .installmentMonths(payment.getInstallmentMonths())
            .approvedAt(payment.getApprovedAt())
            .cancelledAt(payment.getCancelledAt())
            .cancelReason(payment.getCancelReason())
            .receiptUrl(payment.getReceiptUrl())
            .cashReceiptNumber(payment.getCashReceiptNumber())
            .cashReceiptType(payment.getCashReceiptType())
            .createdAt(payment.getCreatedAt())
            .build();
    }

    private PaymentRefundResponse buildRefundResponse(PaymentRefund refund) {
        return PaymentRefundResponse.builder()
            .id(refund.getId())
            .paymentId(refund.getPaymentId())
            .refundAmount(refund.getRefundAmount())
            .refundReason(refund.getRefundReason())
            .refundStatus(refund.getRefundStatus())
            .pgRefundId(refund.getPgRefundId())
            .refundedAt(refund.getRefundedAt())
            .createdAt(refund.getCreatedAt())
            .build();
    }
}
