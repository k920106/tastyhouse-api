package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.product.dto.TodayDiscountProductDto;
import com.tastyhouse.core.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCoreService {

    private final ProductRepository productRepository;

    public TodayDiscountProductPageResult findTodayDiscountProducts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<TodayDiscountProductDto> productPage = productRepository.findTodayDiscountProducts(pageRequest);

        return new TodayDiscountProductPageResult(
            productPage.getContent(),
            productPage.getTotalElements(),
            productPage.getTotalPages(),
            productPage.getNumber(),
            productPage.getSize()
        );
    }

    public static class TodayDiscountProductPageResult {
        private final List<TodayDiscountProductDto> content;
        private final long totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;

        public TodayDiscountProductPageResult(List<TodayDiscountProductDto> content, long totalElements, int totalPages, int currentPage, int pageSize) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }

        public List<TodayDiscountProductDto> getContent() { return content; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
    }
}
