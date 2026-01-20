package com.tastyhouse.webapi.partnership;

import com.tastyhouse.core.entity.partnership.PartnershipRequest;
import com.tastyhouse.core.repository.partnership.PartnershipRequestJpaRepository;
import com.tastyhouse.webapi.partnership.request.PartnershipRequestCreateRequest;
import com.tastyhouse.webapi.partnership.response.PartnershipRequestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartnershipRequestService {

    private final PartnershipRequestJpaRepository partnershipRequestJpaRepository;

    @Transactional
    public PartnershipRequestResponse createPartnershipRequest(PartnershipRequestCreateRequest request) {
        PartnershipRequest partnershipRequest = PartnershipRequest.of(
            request.businessName(),
            request.address(),
            request.addressDetail(),
            request.contactName(),
            request.contactPhone(),
            request.consultationRequestedAt()
        );

        PartnershipRequest savedRequest = partnershipRequestJpaRepository.save(partnershipRequest);

        return PartnershipRequestResponse.builder()
            .id(savedRequest.getId())
            .businessName(savedRequest.getBusinessName())
            .address(savedRequest.getAddress())
            .addressDetail(savedRequest.getAddressDetail())
            .contactName(savedRequest.getContactName())
            .contactPhone(savedRequest.getContactPhone())
            .consultationRequestedAt(savedRequest.getConsultationRequestedAt())
            .createdAt(savedRequest.getCreatedAt())
            .build();
    }
}
