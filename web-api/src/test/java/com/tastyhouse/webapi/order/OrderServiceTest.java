package com.tastyhouse.webapi.order;

import com.tastyhouse.core.entity.order.Order;
import com.tastyhouse.core.entity.order.OrderItem;
import com.tastyhouse.core.entity.order.OrderStatus;
import com.tastyhouse.core.entity.place.Place;
import com.tastyhouse.core.entity.product.Product;
import com.tastyhouse.core.exception.AccessDeniedException;
import com.tastyhouse.core.exception.BusinessException;
import com.tastyhouse.core.exception.EntityNotFoundException;
import com.tastyhouse.core.repository.product.ProductImageJpaRepository;
import com.tastyhouse.core.service.*;
import com.tastyhouse.webapi.member.MemberService;
import com.tastyhouse.webapi.member.response.MemberProfileResponse;
import com.tastyhouse.webapi.order.request.OrderCreateRequest;
import com.tastyhouse.webapi.order.request.OrderItemRequest;
import com.tastyhouse.webapi.order.response.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderCoreService orderCoreService;
    @Mock private PointCoreService pointCoreService;
    @Mock private CouponCoreService couponCoreService;
    @Mock private ProductCoreService productCoreService;
    @Mock private PlaceCoreService placeCoreService;
    @Mock private ProductImageJpaRepository productImageJpaRepository;
    @Mock private MemberService memberService;

    @InjectMocks
    private OrderService orderService;

    private static final Long MEMBER_ID = 1L;
    private static final Long PLACE_ID = 1L;
    private static final Long ORDER_ID = 10L;
    private static final Long PRODUCT_ID = 100L;

    private Place mockPlace;
    private MemberProfileResponse memberProfile;
    private Product activeProduct;
    private Order savedOrder;

    @BeforeEach
    void setUp() {
        mockPlace = mock(Place.class);
        given(mockPlace.getName()).willReturn("테스트 매장");
        given(mockPlace.getPhoneNumber()).willReturn("02-1234-5678");

        memberProfile = MemberProfileResponse.builder()
            .id(MEMBER_ID)
            .fullName("홍길동")
            .phoneNumber("010-1234-5678")
            .email("test@test.com")
            .build();

        activeProduct = mock(Product.class);
        given(activeProduct.getId()).willReturn(PRODUCT_ID);
        given(activeProduct.getName()).willReturn("테스트 상품");
        given(activeProduct.getOriginalPrice()).willReturn(10000);
        given(activeProduct.getDiscountPrice()).willReturn(null);
        given(activeProduct.getIsSoldOut()).willReturn(false);

        savedOrder = Order.builder()
            .memberId(MEMBER_ID)
            .placeId(PLACE_ID)
            .orderNumber("ORD20240101000001ABCD1234")
            .orderStatus(OrderStatus.PENDING)
            .ordererName("홍길동")
            .ordererPhone("010-1234-5678")
            .ordererEmail("test@test.com")
            .totalProductAmount(10000)
            .productDiscountAmount(0)
            .couponDiscountAmount(0)
            .pointDiscountAmount(0)
            .totalDiscountAmount(0)
            .finalAmount(10000)
            .usedPoint(0)
            .earnedPoint(0)
            .build();
    }

    @Nested
    @DisplayName("주문 생성 (createOrder)")
    class CreateOrderTest {

        @Test
        @DisplayName("쿠폰/포인트 없이 단순 상품 주문 생성 성공")
        void createOrder_simpleOrder_success() {
            // Given
            List<OrderItemRequest> items = List.of(new OrderItemRequest(PRODUCT_ID, null, 1));
            OrderCreateRequest request = new OrderCreateRequest(
                PLACE_ID, items, null, 0,
                10000, 0, 0, 0, 10000
            );

            OrderItem savedOrderItem = mock(OrderItem.class);
            given(savedOrderItem.getId()).willReturn(1L);

            given(placeCoreService.findPlaceById(PLACE_ID)).willReturn(mockPlace);
            given(memberService.getMemberProfile(MEMBER_ID)).willReturn(Optional.of(memberProfile));
            given(orderCoreService.saveOrder(any(Order.class))).willReturn(savedOrder);
            given(productCoreService.findProductById(PRODUCT_ID)).willReturn(Optional.of(activeProduct));
            given(productImageJpaRepository.findByProductIdAndIsActiveTrueOrderBySortAsc(PRODUCT_ID))
                .willReturn(Collections.emptyList());
            given(orderCoreService.saveOrderItem(any(OrderItem.class))).willReturn(savedOrderItem);
            given(orderCoreService.findOrderItemsByOrderId(any())).willReturn(List.of(savedOrderItem));
            given(orderCoreService.findOrderItemOptionsByOrderItemId(any())).willReturn(Collections.emptyList());
            given(orderCoreService.findPaymentByOrderId(any())).willReturn(Optional.empty());

            OrderItem orderItemWithPrices = mock(OrderItem.class);
            given(savedOrderItem.getProductId()).willReturn(PRODUCT_ID);
            given(savedOrderItem.getProductName()).willReturn("테스트 상품");
            given(savedOrderItem.getProductImageUrl()).willReturn(null);
            given(savedOrderItem.getQuantity()).willReturn(1);
            given(savedOrderItem.getUnitPrice()).willReturn(10000);
            given(savedOrderItem.getDiscountPrice()).willReturn(null);
            given(savedOrderItem.getOptionTotalPrice()).willReturn(0);
            given(savedOrderItem.getTotalPrice()).willReturn(10000);

            // When
            OrderResponse result = orderService.createOrder(MEMBER_ID, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getOrderNumber()).isEqualTo("ORD20240101000001ABCD1234");
            assertThat(result.getFinalAmount()).isEqualTo(10000);
            verify(orderCoreService).saveOrder(any(Order.class));
            verify(orderCoreService).saveOrderItem(any(OrderItem.class));
        }

        @Test
        @DisplayName("회원 정보가 없으면 EntityNotFoundException 발생")
        void createOrder_memberNotFound_throwsException() {
            // Given
            List<OrderItemRequest> items = List.of(new OrderItemRequest(PRODUCT_ID, null, 1));
            OrderCreateRequest request = new OrderCreateRequest(
                PLACE_ID, items, null, 0,
                10000, 0, 0, 0, 10000
            );

            given(placeCoreService.findPlaceById(PLACE_ID)).willReturn(mockPlace);
            given(memberService.getMemberProfile(MEMBER_ID)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> orderService.createOrder(MEMBER_ID, request))
                .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("품절 상품 주문 시 BusinessException 발생")
        void createOrder_soldOutProduct_throwsException() {
            // Given
            Product soldOutProduct = mock(Product.class);
            given(soldOutProduct.getId()).willReturn(PRODUCT_ID);
            given(soldOutProduct.getName()).willReturn("품절 상품");
            given(soldOutProduct.getIsSoldOut()).willReturn(true);

            List<OrderItemRequest> items = List.of(new OrderItemRequest(PRODUCT_ID, null, 1));
            OrderCreateRequest request = new OrderCreateRequest(
                PLACE_ID, items, null, 0,
                10000, 0, 0, 0, 10000
            );

            given(placeCoreService.findPlaceById(PLACE_ID)).willReturn(mockPlace);
            given(memberService.getMemberProfile(MEMBER_ID)).willReturn(Optional.of(memberProfile));
            given(orderCoreService.saveOrder(any(Order.class))).willReturn(savedOrder);
            given(productCoreService.findProductById(PRODUCT_ID)).willReturn(Optional.of(soldOutProduct));
            given(productImageJpaRepository.findByProductIdAndIsActiveTrueOrderBySortAsc(PRODUCT_ID))
                .willReturn(Collections.emptyList());

            // When & Then
            assertThatThrownBy(() -> orderService.createOrder(MEMBER_ID, request))
                .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("상품 금액 불일치 시 BusinessException 발생")
        void createOrder_productAmountMismatch_throwsException() {
            // Given
            List<OrderItemRequest> items = List.of(new OrderItemRequest(PRODUCT_ID, null, 1));
            OrderCreateRequest request = new OrderCreateRequest(
                PLACE_ID, items, null, 0,
                99999,  // 잘못된 금액
                0, 0, 0, 99999
            );

            OrderItem savedOrderItem = mock(OrderItem.class);
            given(savedOrderItem.getId()).willReturn(1L);

            given(placeCoreService.findPlaceById(PLACE_ID)).willReturn(mockPlace);
            given(memberService.getMemberProfile(MEMBER_ID)).willReturn(Optional.of(memberProfile));
            given(orderCoreService.saveOrder(any(Order.class))).willReturn(savedOrder);
            given(productCoreService.findProductById(PRODUCT_ID)).willReturn(Optional.of(activeProduct));
            given(productImageJpaRepository.findByProductIdAndIsActiveTrueOrderBySortAsc(PRODUCT_ID))
                .willReturn(Collections.emptyList());
            given(orderCoreService.saveOrderItem(any(OrderItem.class))).willReturn(savedOrderItem);

            // When & Then
            assertThatThrownBy(() -> orderService.createOrder(MEMBER_ID, request))
                .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("존재하지 않는 상품 주문 시 EntityNotFoundException 발생")
        void createOrder_productNotFound_throwsException() {
            // Given
            List<OrderItemRequest> items = List.of(new OrderItemRequest(PRODUCT_ID, null, 1));
            OrderCreateRequest request = new OrderCreateRequest(
                PLACE_ID, items, null, 0,
                10000, 0, 0, 0, 10000
            );

            given(placeCoreService.findPlaceById(PLACE_ID)).willReturn(mockPlace);
            given(memberService.getMemberProfile(MEMBER_ID)).willReturn(Optional.of(memberProfile));
            given(orderCoreService.saveOrder(any(Order.class))).willReturn(savedOrder);
            given(productCoreService.findProductById(PRODUCT_ID)).willReturn(Optional.empty());
            given(productImageJpaRepository.findByProductIdAndIsActiveTrueOrderBySortAsc(PRODUCT_ID))
                .willReturn(Collections.emptyList());

            // When & Then
            assertThatThrownBy(() -> orderService.createOrder(MEMBER_ID, request))
                .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("주문 상세 조회 (getOrderDetail)")
    class GetOrderDetailTest {

        @Test
        @DisplayName("본인 주문 상세 조회 성공")
        void getOrderDetail_success() {
            // Given
            given(orderCoreService.findOrderById(ORDER_ID)).willReturn(Optional.of(savedOrder));
            given(placeCoreService.findPlaceById(PLACE_ID)).willReturn(mockPlace);
            given(orderCoreService.findOrderItemsByOrderId(any())).willReturn(Collections.emptyList());
            given(orderCoreService.findPaymentByOrderId(any())).willReturn(Optional.empty());

            // When
            OrderResponse result = orderService.getOrderDetail(MEMBER_ID, ORDER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getOrdererName()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("존재하지 않는 주문 조회 시 EntityNotFoundException 발생")
        void getOrderDetail_orderNotFound() {
            // Given
            given(orderCoreService.findOrderById(ORDER_ID)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> orderService.getOrderDetail(MEMBER_ID, ORDER_ID))
                .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("다른 회원의 주문 조회 시 AccessDeniedException 발생")
        void getOrderDetail_otherMemberOrder() {
            // Given
            Long otherMemberId = 999L;
            given(orderCoreService.findOrderById(ORDER_ID)).willReturn(Optional.of(savedOrder));

            // When & Then
            assertThatThrownBy(() -> orderService.getOrderDetail(otherMemberId, ORDER_ID))
                .isInstanceOf(AccessDeniedException.class);
        }
    }
}
