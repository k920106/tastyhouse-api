package com.tastyhouse.adminapi.faq;

import com.tastyhouse.adminapi.common.PageRequest;
import com.tastyhouse.adminapi.common.PageResult;
import com.tastyhouse.adminapi.exception.ResourceNotFoundException;
import com.tastyhouse.adminapi.faq.request.FaqCreateRequest;
import com.tastyhouse.adminapi.faq.response.FaqDetailResponse;
import com.tastyhouse.adminapi.faq.response.FaqListItem;
import com.tastyhouse.core.entity.company.Company;
import com.tastyhouse.core.entity.faq.Faq;
import com.tastyhouse.core.entity.faq.dto.FaqListItemDto;
import com.tastyhouse.core.service.CompanyCoreService;
import com.tastyhouse.core.service.FaqCoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqCoreService faqCoreService;
    private final CompanyCoreService companyCoreService;

    public PageResult<FaqListItem> findFaqList(Long companyId, String title, Boolean active, PageRequest pageRequest) {
        FaqCoreService.FaqPageResult coreResult = faqCoreService.findAllWithPagination(
            companyId, title, active,
            pageRequest.getPage(), pageRequest.getSize()
        );

        List<FaqListItem> faqListItems = coreResult.getContent().stream()
            .map(this::convertToFaqListItem)
            .toList();

        return new PageResult<>(
            faqListItems,
            coreResult.getTotalElements(),
            coreResult.getTotalPages(),
            coreResult.getCurrentPage(),
            coreResult.getPageSize()
        );
    }

    public Long createFaq(FaqCreateRequest request) {
        Faq faq = faqCoreService.createFaq(
            request.getCompanyId(),
            request.getTitle(),
            request.getContent(),
            request.isActive(),
            request.getSort()
        );
        return faq.getId();
    }

    public FaqDetailResponse findFaqDetail(Long id) {
        Faq faq = faqCoreService.findById(id);
        if (faq == null) {
            throw new ResourceNotFoundException("FAQ를 찾을 수 없습니다. ID: " + id);
        }

        Company company = companyCoreService.findById(faq.getCompanyId());

        return FaqDetailResponse.builder()
            .id(faq.getId())
            .companyId(faq.getCompanyId())
            .companyName(company.getName())
            .title(faq.getTitle())
            .content(faq.getContent())
            .active(faq.getActive())
            .sort(faq.getSort())
            .createdAt(faq.getCreatedAt())
            .updatedAt(faq.getUpdatedAt())
            .build();
    }

    public Long updateFaq(Long id, com.tastyhouse.adminapi.faq.request.FaqUpdateRequest request) {
        Faq faq = faqCoreService.updateFaq(
            id,
            request.getCompanyId(),
            request.getTitle(),
            request.getContent(),
            request.getActive(),
            request.getSort()
        );

        if (faq == null) {
            throw new ResourceNotFoundException("FAQ를 찾을 수 없습니다. ID: " + id);
        }

        return faq.getId();
    }

    private FaqListItem convertToFaqListItem(FaqListItemDto dto) {
        return FaqListItem.builder()
            .id(dto.getId())
            .title(dto.getTitle())
            .active(dto.getActive())
            .sort(dto.getSort())
            .build();
    }
}
