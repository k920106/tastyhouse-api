package com.tastyhouse.webapi.partnership.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PartnershipRequestResponse(
    Long id,
    String businessName,
    String address,
    String addressDetail,
    String contactName,
    String contactPhone,
    LocalDateTime consultationRequestedAt,
    LocalDateTime createdAt
) {
}
