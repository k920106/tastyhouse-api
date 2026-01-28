package com.tastyhouse.webapi.payment.request;

import com.tastyhouse.core.entity.payment.PgProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentConfirmRequest(
    @NotNull(message = "결제 ID는 필수입니다")
    Long paymentId,

    @NotNull(message = "PG사는 필수입니다")
    PgProvider pgProvider,

    @NotBlank(message = "PG 거래 ID는 필수입니다")
    String pgTid,

    String pgOrderId,

    String cardCompany,

    String cardNumber,

    Integer installmentMonths,

    String receiptUrl
) {
}
