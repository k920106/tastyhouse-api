package com.tastyhouse.webapi.order;

import com.tastyhouse.core.entity.coupon.Coupon;
import com.tastyhouse.core.entity.coupon.DiscountType;
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
import com.tastyhouse.webapi.member.MemberService;
import com.tastyhouse.webapi.member.response.MemberContactResponse;
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
    private final MemberService memberService;

    @Transactional
    public OrderResponse createOrder(Long memberId, OrderCreateRequest request) {
        Place place = placeJpaRepository.findById(request.placeId())
            .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다."));

        MemberContactResponse contact = memberService.getMemberContact(memberId)
            .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        int totalProductAmount = 0;
        int productDiscountAmount = 0;

        Order order = Order.builder()
            .memberId(memberId)
            .placeId(request.placeId())
            .orderNumber(generateOrderNumber())
            .orderStatus(OrderStatus.PENDING)
            .ordererName(contact.getFullName())
            .ordererPhone(contact.getPhoneNumber())
            .ordererEmail(contact.getEmail())
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

            if (itemRequest.selectedOptions() != null) {
                for (OrderItemOptionRequest optionRequest : itemRequest.selectedOptions()) {
                    ProductOptionGroup optionGroup = productOptionGroupJpaRepository.findById(optionRequest.groupId())
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

            // 쿠폰 타입에 따라 할인 금액 계산
            if (coupon.getDiscountType() == DiscountType.AMOUNT) {
                // 정액 할인
                couponDiscountAmount = coupon.getDiscountAmount();
            } else if (coupon.getDiscountType() == DiscountType.RATE) {
                // 정률 할인 - 상품 할인 후 금액 기준으로 계산
                couponDiscountAmount = (int) Math.round(orderAmountAfterProductDiscount * coupon.getDiscountAmount() / 100.0);
                // 최대 할인 금액 제한 적용
                if (coupon.getMaxDiscountAmount() != null && couponDiscountAmount > coupon.getMaxDiscountAmount()) {
                    couponDiscountAmount = coupon.getMaxDiscountAmount();
                }
            }

            memberCouponId = memberCoupon.getId();
            memberCoupon.use();
        }

        int pointDiscountAmount = 0;
        if (request.usePoint() > 0) {
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

        validateOrderAmounts(request, totalProductAmount, totalDiscountAmount,
            productDiscountAmount, couponDiscountAmount, pointDiscountAmount, finalAmount);

        savedOrder.updateAmounts(totalProductAmount, productDiscountAmount,
            couponDiscountAmount, pointDiscountAmount, totalDiscountAmount,
            finalAmount, memberCouponId, pointDiscountAmount);

        return buildOrderResponse(savedOrder, place.getName());
    }

    private void validateOrderAmounts(OrderCreateRequest request,
                                      int totalProductAmount, int totalDiscountAmount,
                                      int productDiscountAmount, int couponDiscountAmount,
                                      int pointDiscountAmount, int finalAmount) {
        if (!request.totalProductAmount().equals(totalProductAmount)) {
            throw new IllegalArgumentException(
                "상품 금액이 일치하지 않습니다. 요청: " + request.totalProductAmount() + ", 계산: " + totalProductAmount);
        }
        if (!request.productDiscountAmount().equals(productDiscountAmount)) {
            throw new IllegalArgumentException(
                "상품 할인 금액이 일치하지 않습니다. 요청: " + request.productDiscountAmount() + ", 계산: " + productDiscountAmount);
        }
        if (!request.couponDiscountAmount().equals(couponDiscountAmount)) {
            throw new IllegalArgumentException(
                "쿠폰 사용 금액이 일치하지 않습니다. 요청: " + request.couponDiscountAmount() + ", 계산: " + couponDiscountAmount);
        }
        if (!request.usePoint().equals(pointDiscountAmount)) {
            throw new IllegalArgumentException(
                "포인트 사용 금액이 일치하지 않습니다. 요청: " + request.usePoint() + ", 계산: " + pointDiscountAmount);
        }
        if (!request.totalDiscountAmount().equals(totalDiscountAmount)) {
            throw new IllegalArgumentException(
                "할인 금액이 일치하지 않습니다. 요청: " + request.totalDiscountAmount() + ", 계산: " + totalDiscountAmount);
        }
        if (!request.finalAmount().equals(finalAmount)) {
            throw new IllegalArgumentException(
                "결제 금액이 일치하지 않습니다. 요청: " + request.finalAmount() + ", 계산: " + finalAmount);
        }
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
                .cardCompany(payment.getCardCompany())
                .cardNumber(payment.getCardNumber())
                .approvedAt(payment.getApprovedAt())
                .receiptUrl(payment.getReceiptUrl())
                .build();
        }

        return OrderResponse.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .paymentStatus(payment != null ? payment.getPaymentStatus() : null)
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
            .approvedAt(payment != null ? payment.getApprovedAt() : null)
            .createdAt(order.getCreatedAt())
            .build();
    }
}
