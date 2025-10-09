package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.faq.Faq;
import com.tastyhouse.core.entity.faq.dto.FaqListItemDto;
import com.tastyhouse.core.repository.faq.FaqJpaRepository;
import com.tastyhouse.core.repository.faq.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqCoreService {

    private final FaqRepository faqRepository;
    private final FaqJpaRepository faqJpaRepository;

    public FaqPageResult findAllWithPagination(Long companyId, String title, Boolean active, LocalDate startDate, LocalDate endDate,
                                                 int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<FaqListItemDto> faqPage = faqRepository.findAllWithFilter(
            companyId, title, active, startDate, endDate, pageRequest
        );

        return new FaqPageResult(
            faqPage.getContent(),
            faqPage.getTotalElements(),
            faqPage.getTotalPages(),
            faqPage.getNumber(),
            faqPage.getSize()
        );
    }

    public Faq findById(Long id) {
        return faqJpaRepository.findById(id).orElse(null);
    }

    public Faq createFaq(Long companyId, String title, String content, Boolean active, Integer sort) {
        Faq faq = new Faq();
        faq.setCompanyId(companyId);
        faq.setTitle(title);
        faq.setContent(content);
        faq.setActive(active != null ? active : true);
        faq.setSort(sort != null ? sort : 0);

        return faqJpaRepository.save(faq);
    }

    public Faq updateFaq(Long id, Long companyId, String title, String content, Boolean active, Integer sort) {
        Faq faq = faqJpaRepository.findById(id).orElse(null);
        if (faq == null) {
            return null;
        }

        if (companyId != null) {
            faq.setCompanyId(companyId);
        }
        if (title != null) {
            faq.setTitle(title);
        }
        if (content != null) {
            faq.setContent(content);
        }
        if (active != null) {
            faq.setActive(active);
        }
        if (sort != null) {
            faq.setSort(sort);
        }

        return faqJpaRepository.save(faq);
    }

    public static class FaqPageResult {
        private final List<FaqListItemDto> content;
        private final long totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;

        public FaqPageResult(List<FaqListItemDto> content, long totalElements, int totalPages, int currentPage, int pageSize) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }

        public List<FaqListItemDto> getContent() { return content; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
    }
}
