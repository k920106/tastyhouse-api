package com.tastyhouse.webapi.policy;

import com.tastyhouse.core.entity.policy.PolicyType;
import com.tastyhouse.core.entity.policy.dto.PolicyDocumentDto;
import com.tastyhouse.core.entity.policy.dto.PolicyListItemDto;
import com.tastyhouse.core.service.PolicyDocumentCoreService;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.policy.request.PolicyCreateRequest;
import com.tastyhouse.webapi.policy.request.PolicyUpdateRequest;
import com.tastyhouse.webapi.policy.response.PolicyDetailResponse;
import com.tastyhouse.webapi.policy.response.PolicyListItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyDocumentCoreService policyDocumentCoreService;

    public PolicyDetailResponse findCurrentByType(PolicyType type) {
        PolicyDocumentDto dto = policyDocumentCoreService.findCurrentByType(type)
            .orElseThrow(() -> new IllegalArgumentException("현재 유효한 정책을 찾을 수 없습니다."));

        return convertToPolicyDetailResponse(dto);
    }

    public PolicyDetailResponse findByTypeAndVersion(PolicyType type, String version) {
        PolicyDocumentDto dto = policyDocumentCoreService.findByTypeAndVersion(type, version)
            .orElseThrow(() -> new IllegalArgumentException("해당 버전의 정책을 찾을 수 없습니다."));

        return convertToPolicyDetailResponse(dto);
    }

    public PageResult<PolicyListItemResponse> findAllByType(PolicyType type, PageRequest pageRequest) {
        PolicyDocumentCoreService.PolicyPageResult coreResult = policyDocumentCoreService
            .findAllByTypeWithPagination(type, pageRequest.getPage(), pageRequest.getSize());

        List<PolicyListItemResponse> policyListItems = coreResult.getContent().stream()
            .map(this::convertToPolicyListItemResponse)
            .toList();

        return new PageResult<>(
            policyListItems,
            coreResult.getTotalElements(),
            coreResult.getTotalPages(),
            coreResult.getCurrentPage(),
            coreResult.getPageSize()
        );
    }

    private PolicyDetailResponse convertToPolicyDetailResponse(PolicyDocumentDto dto) {
        return PolicyDetailResponse.builder()
            .id(dto.getId())
            .type(dto.getType())
            .version(dto.getVersion())
            .title(dto.getTitle())
            .content(dto.getContent())
            .current(dto.getCurrent())
            .mandatory(dto.getMandatory())
            .effectiveDate(dto.getEffectiveDate())
            .createdAt(dto.getCreatedAt())
            .updatedAt(dto.getUpdatedAt())
            .build();
    }

    private PolicyListItemResponse convertToPolicyListItemResponse(PolicyListItemDto dto) {
        return PolicyListItemResponse.builder()
            .id(dto.getId())
            .type(dto.getType())
            .version(dto.getVersion())
            .title(dto.getTitle())
            .current(dto.getCurrent())
            .effectiveDate(dto.getEffectiveDate())
            .createdAt(dto.getCreatedAt())
            .build();
    }

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
