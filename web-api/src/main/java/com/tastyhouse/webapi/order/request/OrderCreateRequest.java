package com.tastyhouse.webapi.order.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OrderCreateRequest(
    @NotNull(message = "매장 ID는 필수입니다")
    Long placeId,

    @NotBlank(message = "주문자명은 필수입니다")
    @Size(max = 100, message = "주문자명은 100자 이내로 입력해주세요")
    String ordererName,

    @NotBlank(message = "휴대폰 번호는 필수입니다")
    @Size(max = 20, message = "휴대폰 번호는 20자 이내로 입력해주세요")
    String ordererPhone,

    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 100자 이내로 입력해주세요")
    String ordererEmail,

    @NotEmpty(message = "주문 상품은 필수입니다")
    @Valid
    List<OrderItemRequest> orderItems,

    Long memberCouponId,

    Integer usePoint,

    @NotNull(message = "약관 동의는 필수입니다")
    Boolean agreementConfirmed
) {
}
