package com.tastyhouse.webapi.order;

import com.tastyhouse.core.entity.coupon.Coupon;
import com.tastyhouse.core.entity.coupon.MemberCoupon;
import com.tastyhouse.core.entity.order.Order;
import com.tastyhouse.core.entity.order.OrderItem;
import com.tastyhouse.core.entity.order.OrderItemOption;
import com.tastyhouse.core.entity.order.OrderStatus;
import com.tastyhouse.core.entity.payment.Payment;
import com.tastyhouse.core.entity.place.Place;
import com.tastyhouse.core.entity.point.MemberPoint;
import com.tastyhouse.core.entity.point.MemberPointHistory;
import com.tastyhouse.core.entity.point.PointType;
import com.tastyhouse.core.entity.product.Product;
import com.tastyhouse.core.entity.product.ProductOption;
import com.tastyhouse.core.entity.product.ProductOptionGroup;
import com.tastyhouse.core.repository.coupon.CouponJpaRepository;
import com.tastyhouse.core.repository.coupon.MemberCouponJpaRepository;
import com.tastyhouse.core.repository.order.OrderItemJpaRepository;
import com.tastyhouse.core.repository.order.OrderItemOptionJpaRepository;
import com.tastyhouse.core.repository.order.OrderJpaRepository;
import com.tastyhouse.core.repository.payment.PaymentJpaRepository;
import com.tastyhouse.core.repository.place.PlaceJpaRepository;
import com.tastyhouse.core.repository.point.MemberPointHistoryJpaRepository;
import com.tastyhouse.core.repository.point.MemberPointJpaRepository;
import com.tastyhouse.core.repository.product.ProductJpaRepository;
import com.tastyhouse.core.repository.product.ProductImageJpaRepository;
import com.tastyhouse.core.repository.product.ProductOptionGroupJpaRepository;
import com.tastyhouse.core.repository.product.ProductOptionJpaRepository;
import com.tastyhouse.webapi.order.request.OrderCancelRequest;
import com.tastyhouse.webapi.order.request.OrderCreateRequest;
import com.tastyhouse.webapi.order.request.OrderItemOptionRequest;
import com.tastyhouse.webapi.order.request.OrderItemRequest;
import com.tastyhouse.webapi.order.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final OrderItemOptionJpaRepository orderItemOptionJpaRepository;
    private final PaymentJpaRepository paymentJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final ProductImageJpaRepository productImageJpaRepository;
    private final ProductOptionGroupJpaRepository productOptionGroupJpaRepository;
    private final ProductOptionJpaRepository productOptionJpaRepository;
    private final PlaceJpaRepository placeJpaRepository;
    private final MemberCouponJpaRepository memberCouponJpaRepository;
    private final CouponJpaRepository couponJpaRepository;
    private final MemberPointJpaRepository memberPointJpaRepository;
    private final MemberPointHistoryJpaRepository memberPointHistoryJpaRepository;

    @Transactional
    public OrderResponse createOrder(Long memberId, OrderCreateRequest request) {
        Place place = placeJpaRepository.findById(request.placeId())
            .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다."));

        int totalProductAmount = 0;
        int productDiscountAmount = 0;

        Order order = Order.builder()
            .memberId(memberId)
            .placeId(request.placeId())
            .orderNumber(generateOrderNumber())
            .orderStatus(OrderStatus.PENDING)
            .ordererName(request.ordererName())
            .ordererPhone(request.ordererPhone())
            .ordererEmail(request.ordererEmail())
            .agreementConfirmed(request.agreementConfirmed())
            .build();

        Order savedOrder = orderJpaRepository.save(order);

        for (OrderItemRequest itemRequest : request.orderItems()) {
            Product product = productJpaRepository.findById(itemRequest.productId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + itemRequest.productId()));

            if (product.getIsSoldOut()) {
                throw new IllegalStateException("품절된 상품입니다: " + product.getName());
            }

            String productImageUrl = productImageJpaRepository.findByProductIdAndIsActiveTrueOrderBySortAsc(product.getId())
                .stream().findFirst().map(img -> img.getImageUrl()).orElse(null);

            int unitPrice = product.getOriginalPrice();
            Integer discountPrice = product.getDiscountPrice();
            int optionTotalPrice = 0;

            OrderItem orderItem = OrderItem.builder()
                .orderId(savedOrder.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImageUrl(productImageUrl)
                .quantity(itemRequest.quantity())
                .unitPrice(unitPrice)
                .discountPrice(discountPrice)
                .build();

            OrderItem savedOrderItem = orderItemJpaRepository.save(orderItem);

            if (itemRequest.options() != null) {
                for (OrderItemOptionRequest optionRequest : itemRequest.options()) {
                    ProductOptionGroup optionGroup = productOptionGroupJpaRepository.findById(optionRequest.optionGroupId())
                        .orElseThrow(() -> new IllegalArgumentException("옵션 그룹을 찾을 수 없습니다."));

                    ProductOption option = productOptionJpaRepository.findById(optionRequest.optionId())
                        .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다."));

                    OrderItemOption orderItemOption = OrderItemOption.builder()
                        .orderItemId(savedOrderItem.getId())
                        .optionGroupId(optionGroup.getId())
                        .optionGroupName(optionGroup.getName())
                        .optionId(option.getId())
                        .optionName(option.getName())
                        .additionalPrice(option.getAdditionalPrice())
                        .build();

                    orderItemOptionJpaRepository.save(orderItemOption);

                    optionTotalPrice += option.getAdditionalPrice();
                }
            }

            int effectivePrice = discountPrice != null ? discountPrice : unitPrice;
            int itemTotal = (effectivePrice + optionTotalPrice) * itemRequest.quantity();
            int itemDiscount = discountPrice != null ? (unitPrice - discountPrice) * itemRequest.quantity() : 0;

            savedOrderItem.updatePrices(optionTotalPrice, itemTotal);

            totalProductAmount += unitPrice * itemRequest.quantity() + optionTotalPrice * itemRequest.quantity();
            productDiscountAmount += itemDiscount;
        }

        int couponDiscountAmount = 0;
        Long memberCouponId = null;
        if (request.memberCouponId() != null) {
            MemberCoupon memberCoupon = memberCouponJpaRepository.findById(request.memberCouponId())
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

            if (!memberCoupon.getMemberId().equals(memberId)) {
                throw new IllegalArgumentException("본인의 쿠폰만 사용할 수 있습니다.");
            }

            if (!memberCoupon.isAvailable()) {
                throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
            }

            Coupon coupon = couponJpaRepository.findById(memberCoupon.getCouponId())
                .orElseThrow(() -> new IllegalArgumentException("쿠폰 정보를 찾을 수 없습니다."));

            int orderAmountAfterProductDiscount = totalProductAmount - productDiscountAmount;
            if (orderAmountAfterProductDiscount < coupon.getMinOrderAmount()) {
                throw new IllegalStateException("최소 주문 금액을 충족하지 않습니다.");
            }

            couponDiscountAmount = coupon.getDiscountAmount();
            memberCouponId = memberCoupon.getId();
            memberCoupon.use();
        }

        int pointDiscountAmount = 0;
        if (request.usePoint() != null && request.usePoint() > 0) {
            MemberPoint memberPoint = memberPointJpaRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("포인트 정보를 찾을 수 없습니다."));

            if (memberPoint.getAvailablePoints() < request.usePoint()) {
                throw new IllegalStateException("포인트가 부족합니다.");
            }

            pointDiscountAmount = request.usePoint();
            memberPoint.deductPoints(pointDiscountAmount);

            MemberPointHistory pointHistory = MemberPointHistory.builder()
                .memberId(memberId)
                .pointType(PointType.USE)
                .pointAmount(-pointDiscountAmount)
                .reason("주문 결제 사용")
                .build();
            memberPointHistoryJpaRepository.save(pointHistory);
        }

        int totalDiscountAmount = productDiscountAmount + couponDiscountAmount + pointDiscountAmount;
        int finalAmount = totalProductAmount - totalDiscountAmount;

        savedOrder.updateAmounts(totalProductAmount, productDiscountAmount,
            couponDiscountAmount, pointDiscountAmount, totalDiscountAmount,
            finalAmount, memberCouponId, pointDiscountAmount);

        return buildOrderResponse(savedOrder, place.getName());
    }

    @Transactional(readOnly = true)
    public List<OrderListItem> getOrderList(Long memberId) {
        List<Order> orders = orderJpaRepository.findByMemberIdOrderByCreatedAtDesc(memberId);

        return orders.stream().map(order -> {
            Place place = placeJpaRepository.findById(order.getPlaceId()).orElse(null);
            List<OrderItem> items = orderItemJpaRepository.findByOrderId(order.getId());
            OrderItem firstItem = items.isEmpty() ? null : items.get(0);

            return OrderListItem.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getOrderStatus())
                .placeName(place != null ? place.getName() : null)
                .firstProductName(firstItem != null ? firstItem.getProductName() : null)
                .firstProductImageUrl(firstItem != null ? firstItem.getProductImageUrl() : null)
                .totalProductCount(items.size())
                .finalAmount(order.getFinalAmount())
                .createdAt(order.getCreatedAt())
                .build();
        }).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderDetail(Long memberId, Long orderId) {
        Order order = orderJpaRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!order.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 주문만 조회할 수 있습니다.");
        }

        Place place = placeJpaRepository.findById(order.getPlaceId()).orElse(null);
        return buildOrderResponse(order, place != null ? place.getName() : null);
    }

    @Transactional
    public OrderResponse cancelOrder(Long memberId, Long orderId, OrderCancelRequest request) {
        Order order = orderJpaRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!order.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 주문만 취소할 수 있습니다.");
        }

        if (order.getOrderStatus() != OrderStatus.PENDING && order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("취소할 수 없는 주문 상태입니다.");
        }

        order.cancel();

        if (order.getUsedPoint() > 0) {
            MemberPoint memberPoint = memberPointJpaRepository.findByMemberId(memberId)
                .orElse(null);
            if (memberPoint != null) {
                memberPoint.addPoints(order.getUsedPoint());

                MemberPointHistory pointHistory = MemberPointHistory.builder()
                    .memberId(memberId)
                    .pointType(PointType.REFUND)
                    .pointAmount(order.getUsedPoint())
                    .reason("주문 취소 환불")
                    .build();
                memberPointHistoryJpaRepository.save(pointHistory);
            }
        }

        Place place = placeJpaRepository.findById(order.getPlaceId()).orElse(null);
        return buildOrderResponse(order, place != null ? place.getName() : null);
    }

    private String generateOrderNumber() {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD" + dateTime + uuid;
    }

    private OrderResponse buildOrderResponse(Order order, String placeName) {
        List<OrderItem> items = orderItemJpaRepository.findByOrderId(order.getId());

        List<OrderItemResponse> itemResponses = items.stream().map(item -> {
            List<OrderItemOption> options = orderItemOptionJpaRepository.findByOrderItemId(item.getId());

            List<OrderItemOptionResponse> optionResponses = options.stream()
                .map(opt -> OrderItemOptionResponse.builder()
                    .id(opt.getId())
                    .optionGroupName(opt.getOptionGroupName())
                    .optionName(opt.getOptionName())
                    .additionalPrice(opt.getAdditionalPrice())
                    .build())
                .toList();

            return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productImageUrl(item.getProductImageUrl())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .discountPrice(item.getDiscountPrice())
                .optionTotalPrice(item.getOptionTotalPrice())
                .totalPrice(item.getTotalPrice())
                .options(optionResponses)
                .build();
        }).toList();

        PaymentSummaryResponse paymentSummary = null;
        Payment payment = paymentJpaRepository.findByOrderId(order.getId()).orElse(null);
        if (payment != null) {
            paymentSummary = PaymentSummaryResponse.builder()
                .id(payment.getId())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .amount(payment.getAmount())
                .approvedAt(payment.getApprovedAt())
                .receiptUrl(payment.getReceiptUrl())
                .build();
        }

        return OrderResponse.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .orderStatus(order.getOrderStatus())
            .placeName(placeName)
            .ordererName(order.getOrdererName())
            .ordererPhone(order.getOrdererPhone())
            .ordererEmail(order.getOrdererEmail())
            .totalProductAmount(order.getTotalProductAmount())
            .productDiscountAmount(order.getProductDiscountAmount())
            .couponDiscountAmount(order.getCouponDiscountAmount())
            .pointDiscountAmount(order.getPointDiscountAmount())
            .totalDiscountAmount(order.getTotalDiscountAmount())
            .finalAmount(order.getFinalAmount())
            .usedPoint(order.getUsedPoint())
            .earnedPoint(order.getEarnedPoint())
            .orderItems(itemResponses)
            .payment(paymentSummary)
            .createdAt(order.getCreatedAt())
            .build();
    }
}
