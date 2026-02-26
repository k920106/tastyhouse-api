package com.tastyhouse.webapi.payment;

import com.tastyhouse.core.entity.order.Order;
import com.tastyhouse.core.entity.order.OrderStatus;
import com.tastyhouse.core.entity.payment.*;
import com.tastyhouse.core.entity.point.MemberPoint;
import com.tastyhouse.core.entity.point.MemberPointHistory;
import com.tastyhouse.core.exception.AccessDeniedException;
import com.tastyhouse.core.exception.BusinessException;
import com.tastyhouse.core.exception.EntityNotFoundException;
import com.tastyhouse.core.repository.order.OrderJpaRepository;
import com.tastyhouse.core.repository.payment.PaymentJpaRepository;
import com.tastyhouse.core.repository.payment.PaymentRefundJpaRepository;
import com.tastyhouse.core.repository.payment.TossPaymentRecordJpaRepository;
import com.tastyhouse.core.repository.point.MemberPointHistoryJpaRepository;
import com.tastyhouse.core.repository.point.MemberPointJpaRepository;
import com.tastyhouse.external.payment.toss.TossPaymentClient;
import com.tastyhouse.external.payment.toss.dto.TossPaymentConfirmResponse;
import com.tastyhouse.webapi.payment.request.PaymentCancelRequest;
import com.tastyhouse.webapi.payment.request.PaymentCreateRequest;
import com.tastyhouse.webapi.payment.request.RefundRequest;
import com.tastyhouse.webapi.payment.request.TossPaymentConfirmApiRequest;
import com.tastyhouse.webapi.payment.response.PaymentCancelCode;
import com.tastyhouse.webapi.payment.response.PaymentCancelResponse;
import com.tastyhouse.webapi.payment.response.PaymentRefundResponse;
import com.tastyhouse.webapi.payment.response.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentJpaRepository paymentJpaRepository;
    @Mock private PaymentRefundJpaRepository paymentRefundJpaRepository;
    @Mock private TossPaymentRecordJpaRepository tossPaymentRecordJpaRepository;
    @Mock private OrderJpaRepository orderJpaRepository;
    @Mock private MemberPointJpaRepository memberPointJpaRepository;
    @Mock private MemberPointHistoryJpaRepository memberPointHistoryJpaRepository;
    @Mock private TossPaymentClient tossPaymentClient;

    @InjectMocks
    private PaymentService paymentService;

    private static final Long MEMBER_ID = 1L;
    private static final Long ORDER_ID = 10L;
    private static final Long PAYMENT_ID = 100L;
    private static final Integer AMOUNT = 20000;

    private Order pendingOrder;
    private Payment pendingPayment;

    @BeforeEach
    void setUp() {
        pendingOrder = Order.builder()
            .memberId(MEMBER_ID)
            .placeId(1L)
            .orderNumber("ORD20240101000001ABCD1234")
            .orderStatus(OrderStatus.PENDING)
            .ordererName("홍길동")
            .ordererPhone("010-1234-5678")
            .ordererEmail("test@test.com")
            .totalProductAmount(AMOUNT)
            .productDiscountAmount(0)
            .couponDiscountAmount(0)
            .pointDiscountAmount(0)
            .totalDiscountAmount(0)
            .finalAmount(AMOUNT)
            .usedPoint(0)
            .earnedPoint(0)
            .build();

        pendingPayment = Payment.builder()
            .orderId(ORDER_ID)
            .paymentMethod(PaymentMethod.CREDIT_CARD)
            .paymentStatus(PaymentStatus.PENDING)
            .amount(AMOUNT)
            .pgOrderId("PG1234567890ABCDEFGH")
            .build();
    }

    @Nested
    @DisplayName("결제 생성 (createPayment)")
    class CreatePaymentTest {

        @Test
        @DisplayName("정상적인 결제 생성 성공")
        void createPayment_success() {
            // Given
            PaymentCreateRequest request = new PaymentCreateRequest(ORDER_ID, PaymentMethod.CREDIT_CARD);

            given(orderJpaRepository.findById(ORDER_ID)).willReturn(Optional.of(pendingOrder));
            given(paymentJpaRepository.existsByOrderId(ORDER_ID)).willReturn(false);
            given(paymentJpaRepository.save(any(Payment.class))).willReturn(pendingPayment);

            // When
            PaymentResponse result = paymentService.createPayment(MEMBER_ID, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getAmount()).isEqualTo(AMOUNT);
            assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
            verify(paymentJpaRepository).save(any(Payment.class));
        }

        @Test
        @DisplayName("주문이 존재하지 않으면 EntityNotFoundException 발생")
        void createPayment_orderNotFound() {
            // Given
            PaymentCreateRequest request = new PaymentCreateRequest(ORDER_ID, PaymentMethod.CREDIT_CARD);
            given(orderJpaRepository.findById(ORDER_ID)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> paymentService.createPayment(MEMBER_ID, request))
                .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("다른 회원의 주문에 결제하면 AccessDeniedException 발생")
        void createPayment_otherMemberOrder() {
            // Given
            Long otherMemberId = 999L;
            PaymentCreateRequest request = new PaymentCreateRequest(ORDER_ID, PaymentMethod.CREDIT_CARD);
            given(orderJpaRepository.findById(ORDER_ID)).willReturn(Optional.of(pendingOrder));

            // When & Then
            assertThatThrownBy(() -> paymentService.createPayment(otherMemberId, request))
                .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @DisplayName("PENDING 상태가 아닌 주문에 결제하면 BusinessException 발생")
        void createPayment_invalidOrderStatus() {
            // Given
            Order confirmedOrder = Order.builder()
                .memberId(MEMBER_ID)
                .placeId(1L)
                .orderNumber("ORD20240101000001ABCD1235")
                .orderStatus(OrderStatus.CONFIRMED)
                .ordererName("홍길동")
                .ordererPhone("010-1234-5678")
                .totalProductAmount(AMOUNT)
                .productDiscountAmount(0)
                .couponDiscountAmount(0)
                .pointDiscountAmount(0)
                .totalDiscountAmount(0)
                .finalAmount(AMOUNT)
                .usedPoint(0)
                .earnedPoint(0)
                .build();

            PaymentCreateRequest request = new PaymentCreateRequest(ORDER_ID, PaymentMethod.CREDIT_CARD);
            given(orderJpaRepository.findById(ORDER_ID)).willReturn(Optional.of(confirmedOrder));

            // When & Then
            assertThatThrownBy(() -> paymentService.createPayment(MEMBER_ID, request))
                .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("이미 결제가 진행 중인 주문에 결제하면 BusinessException 발생")
        void createPayment_alreadyInProgress() {
            // Given
            PaymentCreateRequest request = new PaymentCreateRequest(ORDER_ID, PaymentMethod.CREDIT_CARD);
            given(orderJpaRepository.findById(ORDER_ID)).willReturn(Optional.of(pendingOrder));
            given(paymentJpaRepository.existsByOrderId(ORDER_ID)).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> paymentService.createPayment(MEMBER_ID, request))
                .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("토스 결제 승인 (confirmTossPayment)")
    class ConfirmTossPaymentTest {

        @Test
        @DisplayName("토스 결제 승인 성공")
        void confirmTossPayment_success() {
            // Given
            TossPaymentConfirmApiRequest request = new TossPaymentConfirmApiRequest(
                "paymentKey123", "PG1234567890ABCDEFGH", AMOUNT
            );

            TossPaymentConfirmResponse tossResponse = mock(TossPaymentConfirmResponse.class);
            given(tossResponse.isError()).willReturn(false);
            given(tossResponse.getPaymentKey()).willReturn("paymentKey123");
            given(tossResponse.getOrderId()).willReturn("PG1234567890ABCDEFGH");
            given(tossResponse.getTotalAmount()).willReturn(AMOUNT);
            given(tossResponse.getCard()).willReturn(null);
            given(tossResponse.getApprovedAt()).willReturn(null);
            given(tossResponse.getReceipt()).willReturn(null);

            given(paymentJpaRepository.findByPgOrderId("PG1234567890ABCDEFGH"))
                .willReturn(Optional.of(pendingPayment));
            given(orderJpaRepository.findById(ORDER_ID)).willReturn(Optional.of(pendingOrder));
            given(tossPaymentClient.confirmPayment(any(), any(), any())).willReturn(tossResponse);
            given(tossPaymentRecordJpaRepository.save(any())).willReturn(null);

            // When
            PaymentResponse result = paymentService.confirmTossPayment(MEMBER_ID, request);

            // Then
            assertThat(result).isNotNull();
            verify(tossPaymentClient).confirmPayment("paymentKey123", "PG1234567890ABCDEFGH", AMOUNT);
        }

        @Test
        @DisplayName("결제 금액 불일치 시 BusinessException 발생")
        void confirmTossPayment_amountMismatch() {
            // Given
            TossPaymentConfirmApiRequest request = new TossPaymentConfirmApiRequest(
                "paymentKey123", "PG1234567890ABCDEFGH", 99999
            );

            given(paymentJpaRepository.findByPgOrderId("PG1234567890ABCDEFGH"))
                .willReturn(Optional.of(pendingPayment));
            given(orderJpaRepository.findById(ORDER_ID)).willReturn(Optional.of(pendingOrder));

            // When & Then
            assertThatThrownBy(() -> paymentService.confirmTossPayment(MEMBER_ID, request))
                .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("토스 API 오류 응답 시 BusinessException 발생 및 결제 실패 처리")
        void confirmTossPayment_tossApiError() {
            // Given
            TossPaymentConfirmApiRequest request = new TossPaymentConfirmApiRequest(
                "paymentKey123", "PG1234567890ABCDEFGH", AMOUNT
            );

            TossPaymentConfirmResponse errorResponse = mock(TossPaymentConfirmResponse.class);
            given(errorResponse.isError()).willReturn(true);
            given(errorResponse.getCode()).willReturn("NOT_FOUND_PAYMENT");
            given(errorResponse.getMessage()).willReturn("존재하지 않는 결제입니다.");

            given(paymentJpaRepository.findByPgOrderId("PG1234567890ABCDEFGH"))
                .willReturn(Optional.of(pendingPayment));
            given(orderJpaRepository.findById(ORDER_ID)).willReturn(Optional.of(pendingOrder));
            given(tossPaymentClient.confirmPayment(any(), any(), any())).willReturn(errorResponse);
            given(tossPaymentRecordJpaRepository.save(any())).willReturn(null);

            // When & Then
            assertThatThrownBy(() -> paymentService.confirmTossPayment(MEMBER_ID, request))
                .isInstanceOf(BusinessException.class);

            assertThat(pendingPayment.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);
        }
    }

    @Nested
    @DisplayName("결제 취소 (cancelPayment)")
    class CancelPaymentTest {

        @Test
        @DisplayName("PENDING 주문의 결제 취소 성공")
        void cancelPayment_pendingOrder_success() {
            // Given
            PaymentCancelRequest request = new PaymentCancelRequest("고객 단순 변심");

            Payment completedPayment = Payment.builder()
                .orderId(ORDER_ID)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentStatus(PaymentStatus.COMPLETED)
                .amount(AMOUNT)
                .pgOrderId("PG1234567890ABCDEFGH")
                .build();

            given(paymentJpaRepository.findById(PAYMENT_ID)).willReturn(Optional.of(completedPayment));
            given(orderJpaRepository.findById(ORDER_ID)).willReturn(Optional.of(pendingOrder));

            // When
            PaymentCancelResponse result = paymentService.cancelPayment(MEMBER_ID, PAYMENT_ID, request);

            // Then
            assertThat(result.code()).isEqualTo(PaymentCancelCode.SUCCESS.name());
        }

        @Test
        @DisplayName("PREPARING 상태 주문은 취소 불가")
        void cancelPayment_preparingOrder_cannotCancel() {
            // Given
            PaymentCancelRequest request = new PaymentCancelRequest("고객 변심");

            Order preparingOrder = Order.builder()
                .memberId(MEMBER_ID)
                .placeId(1L)
                .orderNumber("ORD20240101000001ABCD1237")
                .orderStatus(OrderStatus.PREPARING)
                .ordererName("홍길동")
                .ordererPhone("010-1234-5678")
                .totalProductAmount(AMOUNT)
                .productDiscountAmount(0)
                .couponDiscountAmount(0)
                .pointDiscountAmount(0)
                .totalDiscountAmount(0)
                .finalAmount(AMOUNT)
                .usedPoint(0)
                .earnedPoint(0)
                .build();

            given(paymentJpaRepository.findById(PAYMENT_ID)).willReturn(Optional.of(pendingPayment));
            given(orderJpaRepository.findById(ORDER_ID)).willReturn(Optional.of(preparingOrder));

            // When
            PaymentCancelResponse result = paymentService.cancelPayment(MEMBER_ID, PAYMENT_ID, request);

            // Then
            assertThat(result.code()).isEqualTo(PaymentCancelCode.ALREADY_PREPARING.name());
        }

        @Test
        @DisplayName("CANCELLED 상태 주문은 취소 불가")
        void cancelPayment_alreadyCancelled() {
            // Given
            PaymentCancelRequest request = new PaymentCancelRequest("고객 변심");

            Order cancelledOrder = Order.builder()
                .memberId(MEMBER_ID)
                .placeId(1L)
                .orderNumber("ORD20240101000001ABCD1238")
                .orderStatus(OrderStatus.CANCELLED)
                .ordererName("홍길동")
                .ordererPhone("010-1234-5678")
                .totalProductAmount(AMOUNT)
                .productDiscountAmount(0)
                .couponDiscountAmount(0)
                .pointDiscountAmount(0)
                .totalDiscountAmount(0)
                .finalAmount(AMOUNT)
                .usedPoint(0)
                .earnedPoint(0)
                .build();

            given(paymentJpaRepository.findById(PAYMENT_ID)).willReturn(Optional.of(pendingPayment));
            given(orderJpaRepository.findById(ORDER_ID)).willReturn(Optional.of(cancelledOrder));

            // When
            PaymentCancelResponse result = paymentService.cancelPayment(MEMBER_ID, PAYMENT_ID, request);

            // Then
            assertThat(result.code()).isEqualTo(PaymentCancelCode.ALREADY_CANCELLED.name());
        }

        @Test
        @DisplayName("다른 회원의 결제 취소 시 AccessDeniedException 발생")
        void cancelPayment_otherMember_accessDenied() {
            // Given
            Long otherMemberId = 999L;
            PaymentCancelRequest request = new PaymentCancelRequest("고객 변심");

            given(paymentJpaRepository.findById(PAYMENT_ID)).willReturn(Optional.of(pendingPayment));
            given(orderJpaRepository.findById(ORDER_ID)).willReturn(Optional.of(pendingOrder));

            // When & Then
            assertThatThrownBy(() -> paymentService.cancelPayment(otherMemberId, PAYMENT_ID, request))
                .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @DisplayName("포인트를 사용한 주문 취소 시 포인트 환불 처리")
        void cancelPayment_withUsedPoint_refundsPoint() {
            // Given
            PaymentCancelRequest request = new PaymentCancelRequest("고객 변심");

            Order orderWithPoint = Order.builder()
                .memberId(MEMBER_ID)
                .placeId(1L)
                .orderNumber("ORD20240101000001ABCD1239")
                .orderStatus(OrderStatus.CONFIRMED)
                .ordererName("홍길동")
                .ordererPhone("010-1234-5678")
                .totalProductAmount(AMOUNT)
                .productDiscountAmount(0)
                .couponDiscountAmount(0)
                .pointDiscountAmount(1000)
                .totalDiscountAmount(1000)
                .finalAmount(AMOUNT - 1000)
                .usedPoint(1000)
                .earnedPoint(0)
                .build();

            MemberPoint memberPoint = MemberPoint.builder()
                .memberId(MEMBER_ID)
                .availablePoints(500)
                .build();

            given(paymentJpaRepository.findById(PAYMENT_ID)).willReturn(Optional.of(pendingPayment));
            given(orderJpaRepository.findById(ORDER_ID)).willReturn(Optional.of(orderWithPoint));
            given(memberPointJpaRepository.findByMemberId(MEMBER_ID)).willReturn(Optional.of(memberPoint));
            given(memberPointHistoryJpaRepository.save(any(MemberPointHistory.class))).willReturn(null);

            // When
            PaymentCancelResponse result = paymentService.cancelPayment(MEMBER_ID, PAYMENT_ID, request);

            // Then
            assertThat(result.code()).isEqualTo(PaymentCancelCode.SUCCESS.name());
            assertThat(memberPoint.getAvailablePoints()).isEqualTo(1500);
            verify(memberPointHistoryJpaRepository).save(any(MemberPointHistory.class));
        }
    }

    @Nested
    @DisplayName("환불 요청 (requestRefund)")
    class RequestRefundTest {

        @Test
        @DisplayName("정상 환불 요청 성공")
        void requestRefund_success() {
            // Given
            RefundRequest request = new RefundRequest(10000, "상품 불량");

            Payment completedPayment = Payment.builder()
                .orderId(ORDER_ID)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentStatus(PaymentStatus.COMPLETED)
                .amount(AMOUNT)
                .pgOrderId("PG1234567890ABCDEFGH")
                .build();

            PaymentRefund savedRefund = PaymentRefund.builder()
                .paymentId(PAYMENT_ID)
                .refundAmount(10000)
                .refundReason("상품 불량")
                .build();

            given(paymentJpaRepository.findById(PAYMENT_ID)).willReturn(Optional.of(completedPayment));
            given(orderJpaRepository.findById(ORDER_ID)).willReturn(Optional.of(pendingOrder));
            given(paymentRefundJpaRepository.save(any(PaymentRefund.class))).willReturn(savedRefund);

            // When
            PaymentRefundResponse result = paymentService.requestRefund(MEMBER_ID, PAYMENT_ID, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getRefundAmount()).isEqualTo(10000);
            verify(paymentRefundJpaRepository).save(any(PaymentRefund.class));
        }

        @Test
        @DisplayName("COMPLETED 상태가 아닌 결제에 환불 요청 시 BusinessException 발생")
        void requestRefund_notCompletedPayment() {
            // Given
            RefundRequest request = new RefundRequest(10000, "상품 불량");

            given(paymentJpaRepository.findById(PAYMENT_ID)).willReturn(Optional.of(pendingPayment));
            given(orderJpaRepository.findById(ORDER_ID)).willReturn(Optional.of(pendingOrder));

            // When & Then
            assertThatThrownBy(() -> paymentService.requestRefund(MEMBER_ID, PAYMENT_ID, request))
                .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("환불 금액이 결제 금액 초과 시 BusinessException 발생")
        void requestRefund_amountExceeded() {
            // Given
            RefundRequest request = new RefundRequest(AMOUNT + 1, "상품 불량");

            Payment completedPayment = Payment.builder()
                .orderId(ORDER_ID)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentStatus(PaymentStatus.COMPLETED)
                .amount(AMOUNT)
                .pgOrderId("PG1234567890ABCDEFGH")
                .build();

            given(paymentJpaRepository.findById(PAYMENT_ID)).willReturn(Optional.of(completedPayment));
            given(orderJpaRepository.findById(ORDER_ID)).willReturn(Optional.of(pendingOrder));

            // When & Then
            assertThatThrownBy(() -> paymentService.requestRefund(MEMBER_ID, PAYMENT_ID, request))
                .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("현장결제 완료 (completeOnSitePayment)")
    class CompleteOnSitePaymentTest {

        @Test
        @DisplayName("현장 현금 결제 완료 성공 및 포인트 적립")
        void completeOnSitePayment_cash_success_withPointEarned() {
            // Given
            Payment cashPayment = Payment.builder()
                .orderId(ORDER_ID)
                .paymentMethod(PaymentMethod.CASH_ON_SITE)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(AMOUNT)
                .pgOrderId("PG1234567890ABCDEFGH")
                .build();

            MemberPoint memberPoint = MemberPoint.builder()
                .memberId(MEMBER_ID)
                .availablePoints(0)
                .build();

            given(paymentJpaRepository.findById(PAYMENT_ID)).willReturn(Optional.of(cashPayment));
            given(orderJpaRepository.findById(ORDER_ID)).willReturn(Optional.of(pendingOrder));
            given(memberPointJpaRepository.findByMemberId(MEMBER_ID)).willReturn(Optional.of(memberPoint));
            given(memberPointHistoryJpaRepository.save(any(MemberPointHistory.class))).willReturn(null);

            // When
            PaymentResponse result = paymentService.completeOnSitePayment(MEMBER_ID, PAYMENT_ID);

            // Then
            assertThat(result).isNotNull();
            int expectedEarnedPoint = (int) (AMOUNT * 10 / 100.0);
            assertThat(memberPoint.getAvailablePoints()).isEqualTo(expectedEarnedPoint);
            verify(memberPointHistoryJpaRepository).save(any(MemberPointHistory.class));
        }

        @Test
        @DisplayName("온라인 결제 방식에 현장완료 처리 시 BusinessException 발생")
        void completeOnSitePayment_notOnSiteMethod_throwsException() {
            // Given
            Payment onlinePayment = Payment.builder()
                .orderId(ORDER_ID)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(AMOUNT)
                .pgOrderId("PG1234567890ABCDEFGH")
                .build();

            given(paymentJpaRepository.findById(PAYMENT_ID)).willReturn(Optional.of(onlinePayment));
            given(orderJpaRepository.findById(ORDER_ID)).willReturn(Optional.of(pendingOrder));

            // When & Then
            assertThatThrownBy(() -> paymentService.completeOnSitePayment(MEMBER_ID, PAYMENT_ID))
                .isInstanceOf(BusinessException.class);
        }
    }
}
