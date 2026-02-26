package com.tastyhouse.webapi.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tastyhouse.core.common.PageResult;
import com.tastyhouse.core.exception.AccessDeniedException;
import com.tastyhouse.core.exception.EntityNotFoundException;
import com.tastyhouse.core.exception.ErrorCode;
import com.tastyhouse.webapi.member.response.OrderListItemResponse;
import com.tastyhouse.webapi.order.request.OrderCreateRequest;
import com.tastyhouse.webapi.order.request.OrderItemRequest;
import com.tastyhouse.webapi.order.response.OrderResponse;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderApiController.class)
@Import(OrderApiControllerTest.TestSecurityConfig.class)
class OrderApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private static final Long MEMBER_ID = 1L;
    private static final Long ORDER_ID = 10L;
    private static final Long PLACE_ID = 1L;
    private static final Long PRODUCT_ID = 100L;

    private CustomUserDetails mockUserDetails() {
        com.tastyhouse.core.entity.user.Member member = org.mockito.Mockito.mock(
            com.tastyhouse.core.entity.user.Member.class
        );
        org.mockito.Mockito.when(member.getId()).thenReturn(MEMBER_ID);
        org.mockito.Mockito.when(member.getUsername()).thenReturn("testuser");
        org.mockito.Mockito.when(member.getPassword()).thenReturn("password");
        return new CustomUserDetails(member, java.util.Collections.emptyList());
    }

    private OrderResponse buildOrderResponse() {
        return OrderResponse.builder()
            .id(ORDER_ID)
            .orderNumber("ORD20240101000001ABCD1234")
            .placeName("테스트 매장")
            .ordererName("홍길동")
            .ordererPhone("010-1234-5678")
            .totalProductAmount(10000)
            .productDiscountAmount(0)
            .couponDiscountAmount(0)
            .pointDiscountAmount(0)
            .totalDiscountAmount(0)
            .finalAmount(10000)
            .usedPoint(0)
            .earnedPoint(0)
            .orderItems(Collections.emptyList())
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
    @DisplayName("POST /api/orders/v1 - 주문 생성")
    class CreateOrderTest {

        @Test
        @DisplayName("인증된 사용자의 주문 생성 성공 - 200 OK")
        void createOrder_authenticated_success() throws Exception {
            // Given
            List<OrderItemRequest> items = List.of(new OrderItemRequest(PRODUCT_ID, null, 2));
            OrderCreateRequest request = new OrderCreateRequest(
                PLACE_ID, items, null, 0,
                20000, 0, 0, 0, 20000
            );
            OrderResponse response = buildOrderResponse();

            given(orderService.createOrder(eq(MEMBER_ID), any(OrderCreateRequest.class)))
                .willReturn(response);

            // When & Then
            mockMvc.perform(post("/api/orders/v1")
                    .with(SecurityMockMvcRequestPostProcessors.user(mockUserDetails()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").value("ORD20240101000001ABCD1234"))
                .andExpect(jsonPath("$.data.finalAmount").value(10000));
        }

        @Test
        @DisplayName("인증되지 않은 사용자 - 401 Unauthorized")
        void createOrder_unauthenticated_returns401() throws Exception {
            // Given
            List<OrderItemRequest> items = List.of(new OrderItemRequest(PRODUCT_ID, null, 1));
            OrderCreateRequest request = new OrderCreateRequest(
                PLACE_ID, items, null, 0,
                10000, 0, 0, 0, 10000
            );

            // When & Then
            mockMvc.perform(post("/api/orders/v1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("주문 상품 목록 없이 요청 - 400 Bad Request")
        void createOrder_emptyOrderItems_returns400() throws Exception {
            // Given: 빈 orderItems
            String invalidJson = "{\"placeId\": 1, \"orderItems\": [], \"usePoint\": 0, " +
                "\"totalProductAmount\": 0, \"totalDiscountAmount\": 0, " +
                "\"productDiscountAmount\": 0, \"couponDiscountAmount\": 0, \"finalAmount\": 0}";

            // When & Then
            mockMvc.perform(post("/api/orders/v1")
                    .with(SecurityMockMvcRequestPostProcessors.user(mockUserDetails()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("품절 상품 주문 시 서비스에서 BusinessException 발생")
        void createOrder_soldOutProduct_returnsBadRequest() throws Exception {
            // Given
            List<OrderItemRequest> items = List.of(new OrderItemRequest(PRODUCT_ID, null, 1));
            OrderCreateRequest request = new OrderCreateRequest(
                PLACE_ID, items, null, 0,
                10000, 0, 0, 0, 10000
            );

            given(orderService.createOrder(eq(MEMBER_ID), any(OrderCreateRequest.class)))
                .willThrow(new com.tastyhouse.core.exception.BusinessException(ErrorCode.ORDER_PRODUCT_SOLD_OUT));

            // When & Then
            mockMvc.perform(post("/api/orders/v1")
                    .with(SecurityMockMvcRequestPostProcessors.user(mockUserDetails()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/orders/v1 - 주문 목록 조회")
    class GetOrderListTest {

        @Test
        @DisplayName("주문 목록 조회 성공")
        void getOrderList_success() throws Exception {
            // Given
            PageResult<OrderListItemResponse> pageResult = new PageResult<>(
                Collections.emptyList(), 0L, 0, 0, 10
            );

            given(orderService.getOrderList(eq(MEMBER_ID), any()))
                .willReturn(pageResult);

            // When & Then
            mockMvc.perform(get("/api/orders/v1")
                    .with(SecurityMockMvcRequestPostProcessors.user(mockUserDetails()))
                    .param("page", "0")
                    .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("인증되지 않은 사용자 목록 조회 - 401 Unauthorized")
        void getOrderList_unauthenticated_returns401() throws Exception {
            mockMvc.perform(get("/api/orders/v1"))
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /api/orders/v1/{orderId} - 주문 상세 조회")
    class GetOrderDetailTest {

        @Test
        @DisplayName("주문 상세 조회 성공")
        void getOrderDetail_success() throws Exception {
            // Given
            OrderResponse response = buildOrderResponse();

            given(orderService.getOrderDetail(eq(MEMBER_ID), eq(ORDER_ID)))
                .willReturn(response);

            // When & Then
            mockMvc.perform(get("/api/orders/v1/{orderId}", ORDER_ID)
                    .with(SecurityMockMvcRequestPostProcessors.user(mockUserDetails())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(ORDER_ID))
                .andExpect(jsonPath("$.data.ordererName").value("홍길동"));
        }

        @Test
        @DisplayName("존재하지 않는 주문 조회 시 서비스에서 EntityNotFoundException 발생")
        void getOrderDetail_orderNotFound() throws Exception {
            // Given
            given(orderService.getOrderDetail(eq(MEMBER_ID), eq(ORDER_ID)))
                .willThrow(new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));

            // When & Then
            mockMvc.perform(get("/api/orders/v1/{orderId}", ORDER_ID)
                    .with(SecurityMockMvcRequestPostProcessors.user(mockUserDetails())))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("다른 회원의 주문 조회 시 서비스에서 AccessDeniedException 발생")
        void getOrderDetail_otherMemberOrder_forbidden() throws Exception {
            // Given
            given(orderService.getOrderDetail(eq(MEMBER_ID), eq(ORDER_ID)))
                .willThrow(new AccessDeniedException(ErrorCode.ORDER_ACCESS_DENIED));

            // When & Then
            mockMvc.perform(get("/api/orders/v1/{orderId}", ORDER_ID)
                    .with(SecurityMockMvcRequestPostProcessors.user(mockUserDetails())))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("인증되지 않은 사용자 상세 조회 - 401 Unauthorized")
        void getOrderDetail_unauthenticated_returns401() throws Exception {
            mockMvc.perform(get("/api/orders/v1/{orderId}", ORDER_ID))
                .andExpect(status().isUnauthorized());
        }
    }
}
