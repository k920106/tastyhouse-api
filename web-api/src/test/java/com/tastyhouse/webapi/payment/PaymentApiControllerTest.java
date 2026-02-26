package com.tastyhouse.webapi.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tastyhouse.core.entity.payment.PaymentMethod;
import com.tastyhouse.core.entity.payment.PaymentStatus;
import com.tastyhouse.core.exception.AccessDeniedException;
import com.tastyhouse.core.exception.BusinessException;
import com.tastyhouse.core.exception.EntityNotFoundException;
import com.tastyhouse.core.exception.ErrorCode;
import com.tastyhouse.webapi.payment.request.PaymentCancelRequest;
import com.tastyhouse.webapi.payment.request.PaymentCreateRequest;
import com.tastyhouse.webapi.payment.request.RefundRequest;
import com.tastyhouse.webapi.payment.response.PaymentCancelCode;
import com.tastyhouse.webapi.payment.response.PaymentCancelResponse;
import com.tastyhouse.webapi.payment.response.PaymentRefundResponse;
import com.tastyhouse.webapi.payment.response.PaymentResponse;
import com.tastyhouse.webapi.service.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentApiController.class)
@Import(PaymentApiControllerTest.TestSecurityConfig.class)
class PaymentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    private static final Long MEMBER_ID = 1L;
    private static final Long ORDER_ID = 10L;
    private static final Long PAYMENT_ID = 100L;
    private static final Integer AMOUNT = 20000;

    private CustomUserDetails mockUserDetails() {
        com.tastyhouse.core.entity.user.Member member = org.mockito.Mockito.mock(
            com.tastyhouse.core.entity.user.Member.class
        );
        org.mockito.Mockito.when(member.getId()).thenReturn(MEMBER_ID);
        org.mockito.Mockito.when(member.getUsername()).thenReturn("testuser");
        org.mockito.Mockito.when(member.getPassword()).thenReturn("password");
        return new CustomUserDetails(member, java.util.Collections.emptyList());
    }

    private PaymentResponse buildPaymentResponse(PaymentStatus status) {
        return PaymentResponse.builder()
            .id(PAYMENT_ID)
            .orderId(ORDER_ID)
            .paymentMethod(PaymentMethod.CREDIT_CARD)
            .paymentStatus(status)
            .amount(AMOUNT)
            .pgOrderId("PG1234567890ABCDEFGH")
            .createdAt(LocalDateTime.now())
            .build();
    }

    @org.springframework.boot.test.context.TestConfiguration
    static class TestSecurityConfig {
        @org.springframework.context.annotation.Bean
        public org.springframework.security.web.SecurityFilterChain testSecurityFilterChain(
            org.springframework.security.config.annotation.web.builders.HttpSecurity http
        ) throws Exception {
            http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Nested
    @DisplayName("POST /api/payments/v1 - 결제 생성")
    class CreatePaymentTest {

        @Test
        @DisplayName("인증된 사용자의 결제 생성 성공 - 200 OK")
        void createPayment_authenticated_success() throws Exception {
            // Given
            PaymentCreateRequest request = new PaymentCreateRequest(ORDER_ID, PaymentMethod.CREDIT_CARD);
            PaymentResponse response = buildPaymentResponse(PaymentStatus.PENDING);

            given(paymentService.createPayment(eq(MEMBER_ID), any(PaymentCreateRequest.class)))
                .willReturn(response);

            // When & Then
            mockMvc.perform(post("/api/payments/v1")
                    .with(SecurityMockMvcRequestPostProcessors.user(mockUserDetails()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.paymentStatus").value("PENDING"))
                .andExpect(jsonPath("$.data.amount").value(AMOUNT));
        }

        @Test
        @DisplayName("인증되지 않은 사용자 - 401 Unauthorized")
        void createPayment_unauthenticated_returns401() throws Exception {
            // Given
            PaymentCreateRequest request = new PaymentCreateRequest(ORDER_ID, PaymentMethod.CREDIT_CARD);

            // When & Then
            mockMvc.perform(post("/api/payments/v1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("필수 필드 누락 - 400 Bad Request")
        void createPayment_missingField_returns400() throws Exception {
            // Given: orderId 없이 요청
            String invalidJson = "{\"paymentMethod\": \"CREDIT_CARD\"}";

            // When & Then
            mockMvc.perform(post("/api/payments/v1")
                    .with(SecurityMockMvcRequestPostProcessors.user(mockUserDetails()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/payments/v1/{paymentId}/cancel - 결제 취소")
    class CancelPaymentTest {

        @Test
        @DisplayName("결제 취소 성공 - SUCCESS 코드 반환")
        void cancelPayment_success() throws Exception {
            // Given
            PaymentCancelRequest request = new PaymentCancelRequest("고객 단순 변심");
            PaymentCancelResponse response = PaymentCancelResponse.of(PaymentCancelCode.SUCCESS);

            given(paymentService.cancelPayment(eq(MEMBER_ID), eq(PAYMENT_ID), any(PaymentCancelRequest.class)))
                .willReturn(response);

            // When & Then
            mockMvc.perform(post("/api/payments/v1/{paymentId}/cancel", PAYMENT_ID)
                    .with(SecurityMockMvcRequestPostProcessors.user(mockUserDetails()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("SUCCESS"));
        }

        @Test
        @DisplayName("조리 시작된 주문 취소 시도 - ALREADY_PREPARING 코드 반환")
        void cancelPayment_alreadyPreparing_returnsCode() throws Exception {
            // Given
            PaymentCancelRequest request = new PaymentCancelRequest("고객 변심");
            PaymentCancelResponse response = PaymentCancelResponse.of(PaymentCancelCode.ALREADY_PREPARING);

            given(paymentService.cancelPayment(eq(MEMBER_ID), eq(PAYMENT_ID), any(PaymentCancelRequest.class)))
                .willReturn(response);

            // When & Then
            mockMvc.perform(post("/api/payments/v1/{paymentId}/cancel", PAYMENT_ID)
                    .with(SecurityMockMvcRequestPostProcessors.user(mockUserDetails()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("ALREADY_PREPARING"));
        }

        @Test
        @DisplayName("다른 회원의 결제 취소 시도 - 서비스에서 AccessDeniedException 발생")
        void cancelPayment_otherMember_accessDenied() throws Exception {
            // Given
            PaymentCancelRequest request = new PaymentCancelRequest("고객 변심");

            given(paymentService.cancelPayment(eq(MEMBER_ID), eq(PAYMENT_ID), any(PaymentCancelRequest.class)))
                .willThrow(new AccessDeniedException(ErrorCode.PAYMENT_ACCESS_DENIED));

            // When & Then
            mockMvc.perform(post("/api/payments/v1/{paymentId}/cancel", PAYMENT_ID)
                    .with(SecurityMockMvcRequestPostProcessors.user(mockUserDetails()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/payments/v1/order/{orderId} - 주문별 결제 조회")
    class GetPaymentByOrderIdTest {

        @Test
        @DisplayName("주문별 결제 조회 성공")
        void getPaymentByOrderId_success() throws Exception {
            // Given
            PaymentResponse response = buildPaymentResponse(PaymentStatus.COMPLETED);

            given(paymentService.getPaymentByOrderId(eq(MEMBER_ID), eq(ORDER_ID)))
                .willReturn(response);

            // When & Then
            mockMvc.perform(get("/api/payments/v1/order/{orderId}", ORDER_ID)
                    .with(SecurityMockMvcRequestPostProcessors.user(mockUserDetails())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.paymentStatus").value("COMPLETED"));
        }

        @Test
        @DisplayName("존재하지 않는 결제 조회 시 서비스에서 EntityNotFoundException 발생")
        void getPaymentByOrderId_notFound() throws Exception {
            // Given
            given(paymentService.getPaymentByOrderId(eq(MEMBER_ID), eq(ORDER_ID)))
                .willThrow(new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND, "결제 정보를 찾을 수 없습니다."));

            // When & Then
            mockMvc.perform(get("/api/payments/v1/order/{orderId}", ORDER_ID)
                    .with(SecurityMockMvcRequestPostProcessors.user(mockUserDetails())))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/payments/v1/{paymentId}/refund - 환불 요청")
    class RequestRefundTest {

        @Test
        @DisplayName("환불 요청 성공")
        void requestRefund_success() throws Exception {
            // Given
            RefundRequest request = new RefundRequest(10000, "상품 불량");
            PaymentRefundResponse response = PaymentRefundResponse.builder()
                .id(1L)
                .paymentId(PAYMENT_ID)
                .refundAmount(10000)
                .refundReason("상품 불량")
                .createdAt(LocalDateTime.now())
                .build();

            given(paymentService.requestRefund(eq(MEMBER_ID), eq(PAYMENT_ID), any(RefundRequest.class)))
                .willReturn(response);

            // When & Then
            mockMvc.perform(post("/api/payments/v1/{paymentId}/refund", PAYMENT_ID)
                    .with(SecurityMockMvcRequestPostProcessors.user(mockUserDetails()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.refundAmount").value(10000));
        }

        @Test
        @DisplayName("환불 금액 0원 이하 - 400 Bad Request")
        void requestRefund_invalidAmount_returns400() throws Exception {
            // Given: 환불 금액 0원
            RefundRequest request = new RefundRequest(0, "상품 불량");

            // When & Then
            mockMvc.perform(post("/api/payments/v1/{paymentId}/refund", PAYMENT_ID)
                    .with(SecurityMockMvcRequestPostProcessors.user(mockUserDetails()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("환불 금액 초과 시 서비스에서 BusinessException 발생")
        void requestRefund_amountExceeded() throws Exception {
            // Given
            RefundRequest request = new RefundRequest(99999, "상품 불량");

            given(paymentService.requestRefund(eq(MEMBER_ID), eq(PAYMENT_ID), any(RefundRequest.class)))
                .willThrow(new BusinessException(ErrorCode.PAYMENT_REFUND_AMOUNT_EXCEEDED));

            // When & Then
            mockMvc.perform(post("/api/payments/v1/{paymentId}/refund", PAYMENT_ID)
                    .with(SecurityMockMvcRequestPostProcessors.user(mockUserDetails()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }
    }
}
