package com.tastyhouse.adminapi.policy;

import com.tastyhouse.adminapi.policy.request.PolicyCreateRequest;
import com.tastyhouse.adminapi.policy.request.PolicyUpdateRequest;
import com.tastyhouse.core.service.PolicyDocumentCoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyAdminService {

    private final PolicyDocumentCoreService policyDocumentCoreService;

    @Transactional
    public Long createPolicy(PolicyCreateRequest request) {
        return policyDocumentCoreService.create(
            request.type(),
            request.version(),
            request.title(),
            request.content(),
            request.mandatory(),
            request.effectiveDate(),
            request.createdBy()
        ).getId();
    }

    @Transactional
    public void updatePolicy(Long id, PolicyUpdateRequest request) {
        policyDocumentCoreService.update(
            id,
            request.title(),
            request.content(),
            request.mandatory(),
            request.effectiveDate(),
            request.updatedBy()
        );
    }

    @Transactional
    public void updateCurrentPolicy(Long policyId) {
        policyDocumentCoreService.updateCurrentPolicy(policyId);
    }
}
