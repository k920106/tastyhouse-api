package com.tastyhouse.adminapi.policy;

import com.tastyhouse.adminapi.policy.request.PolicyCreateRequest;
import com.tastyhouse.adminapi.policy.request.PolicyUpdateRequest;
import com.tastyhouse.core.service.PolicyDocumentCoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PolicyAdminService {

    private final PolicyDocumentCoreService policyDocumentCoreService;

    public Long createPolicy(PolicyCreateRequest request) {
        return policyDocumentCoreService.create(
            request.getType(),
            request.getVersion(),
            request.getTitle(),
            request.getContent(),
            request.getMandatory(),
            request.getEffectiveDate(),
            request.getCreatedBy()
        ).getId();
    }

    public void updatePolicy(Long id, PolicyUpdateRequest request) {
        policyDocumentCoreService.update(
            id,
            request.getTitle(),
            request.getContent(),
            request.getMandatory(),
            request.getEffectiveDate(),
            request.getUpdatedBy()
        );
    }

    public void updateCurrentPolicy(Long policyId) {
        policyDocumentCoreService.updateCurrentPolicy(policyId);
    }
}
