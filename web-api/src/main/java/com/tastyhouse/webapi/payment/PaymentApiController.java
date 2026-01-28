package com.tastyhouse.webapi.payment;

import com.tastyhouse.core.common.CommonResponse;
import com.tastyhouse.webapi.payment.request.PaymentCancelRequest;
import com.tastyhouse.webapi.payment.request.PaymentConfirmRequest;
import com.tastyhouse.webapi.payment.request.PaymentCreateRequest;
import com.tastyhouse.webapi.payment.request.RefundRequest;
import com.tastyhouse.webapi.payment.response.PaymentRefundResponse;
import com.tastyhouse.webapi.payment.response.PaymentResponse;
import com.tastyhouse.webapi.service.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payment", description = "결제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentApiController {

    private final PaymentService paymentService;

    @Operation(summary = "결제 생성", description = "주문에 대한 결제를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "결제 생성 성공", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping("/v1")
    public ResponseEntity<CommonResponse<PaymentResponse>> createPayment(
        @Valid @RequestBody PaymentCreateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        PaymentResponse response = paymentService.createPayment(userDetails.getMemberId(), request);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "결제 승인 (PG 콜백)", description = "PG사로부터 결제 승인을 처리합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "결제 승인 성공", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/v1/confirm")
    public ResponseEntity<CommonResponse<PaymentResponse>> confirmPayment(
        @Valid @RequestBody PaymentConfirmRequest request
    ) {
        PaymentResponse response = paymentService.confirmPayment(request);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "주문별 결제 조회", description = "주문에 대한 결제 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "결제 정보를 찾을 수 없음")
    })
    @GetMapping("/v1/order/{orderId}")
    public ResponseEntity<CommonResponse<PaymentResponse>> getPaymentByOrderId(
        @PathVariable Long orderId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        PaymentResponse response = paymentService.getPaymentByOrderId(userDetails.getMemberId(), orderId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "결제 취소", description = "결제를 취소합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "취소 성공", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
        @ApiResponse(responseCode = "400", description = "취소할 수 없는 결제 상태"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "결제를 찾을 수 없음")
    })
    @PostMapping("/v1/{paymentId}/cancel")
    public ResponseEntity<CommonResponse<PaymentResponse>> cancelPayment(
        @PathVariable Long paymentId,
        @Valid @RequestBody PaymentCancelRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        PaymentResponse response = paymentService.cancelPayment(userDetails.getMemberId(), paymentId, request);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "환불 요청", description = "결제에 대한 환불을 요청합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "환불 요청 성공", content = @Content(schema = @Schema(implementation = PaymentRefundResponse.class))),
        @ApiResponse(responseCode = "400", description = "환불할 수 없는 결제 상태"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "결제를 찾을 수 없음")
    })
    @PostMapping("/v1/{paymentId}/refund")
    public ResponseEntity<CommonResponse<PaymentRefundResponse>> requestRefund(
        @PathVariable Long paymentId,
        @Valid @RequestBody RefundRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        PaymentRefundResponse response = paymentService.requestRefund(userDetails.getMemberId(), paymentId, request);
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
