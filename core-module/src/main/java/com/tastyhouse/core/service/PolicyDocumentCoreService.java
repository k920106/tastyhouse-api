package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.policy.PolicyDocument;
import com.tastyhouse.core.entity.policy.PolicyType;
import com.tastyhouse.core.entity.policy.dto.PolicyDocumentDto;
import com.tastyhouse.core.entity.policy.dto.PolicyListItemDto;
import com.tastyhouse.core.repository.policy.PolicyDocumentJpaRepository;
import com.tastyhouse.core.repository.policy.PolicyDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PolicyDocumentCoreService {

    private final PolicyDocumentRepository policyDocumentRepository;
    private final PolicyDocumentJpaRepository policyDocumentJpaRepository;

    public Optional<PolicyDocumentDto> findCurrentByType(PolicyType type) {
        return policyDocumentRepository.findCurrentByType(type);
    }

    public Optional<PolicyDocumentDto> findByTypeAndVersion(PolicyType type, String version) {
        return policyDocumentRepository.findByTypeAndVersion(type, version);
    }

    public PolicyPageResult findAllByTypeWithPagination(PolicyType type, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<PolicyListItemDto> policyPage = policyDocumentRepository.findAllByType(type, pageRequest);

        return new PolicyPageResult(
            policyPage.getContent(),
            policyPage.getTotalElements(),
            policyPage.getTotalPages(),
            policyPage.getNumber(),
            policyPage.getSize()
        );
    }

    @Transactional
    public PolicyDocument create(PolicyType type, String version, String title, String content,
                                 Boolean mandatory, LocalDateTime effectiveDate, String createdBy) {
        PolicyDocument policyDocument = PolicyDocument.builder()
            .type(type)
            .version(version)
            .title(title)
            .content(content)
            .current(false)
            .mandatory(mandatory)
            .effectiveDate(effectiveDate)
            .createdBy(createdBy)
            .build();

        return policyDocumentJpaRepository.save(policyDocument);
    }

    @Transactional
    public void updateCurrentPolicy(Long newPolicyId) {
        PolicyDocument newPolicy = policyDocumentJpaRepository.findById(newPolicyId)
            .orElseThrow(() -> new IllegalArgumentException("정책 문서를 찾을 수 없습니다."));

        Optional<PolicyDocument> currentPolicy = policyDocumentJpaRepository
            .findByTypeAndCurrent(newPolicy.getType(), true);

        currentPolicy.ifPresent(policy -> policy.updateCurrent(false));

        newPolicy.updateCurrent(true);
    }

    @Transactional
    public void update(Long id, String title, String content, Boolean mandatory,
                      LocalDateTime effectiveDate, String updatedBy) {
        PolicyDocument policyDocument = policyDocumentJpaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("정책 문서를 찾을 수 없습니다."));

        policyDocument.update(title, content, mandatory, effectiveDate, updatedBy);
    }

    public static class PolicyPageResult {
        private final List<PolicyListItemDto> content;
        private final long totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;

        public PolicyPageResult(List<PolicyListItemDto> content, long totalElements,
                              int totalPages, int currentPage, int pageSize) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }

        public List<PolicyListItemDto> getContent() { return content; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
    }
}
