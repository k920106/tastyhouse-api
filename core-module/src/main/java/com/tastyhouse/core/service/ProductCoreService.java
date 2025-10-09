package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.product.Product;
import com.tastyhouse.core.entity.product.dto.ProductListItemDto;
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

    public ProductListItemPageResult findProductListItems(Long companyId, String productCode, String name,
                                                         Long brandId, Long supplyId, Boolean display,
                                                         int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProductListItemDto> productPage = productRepository.findAllWithFilterAsDto(
            companyId, productCode, name, brandId, supplyId, display, pageRequest
        );

        return new ProductListItemPageResult(
            productPage.getContent(),
            productPage.getTotalElements(),
            productPage.getTotalPages(),
            productPage.getNumber(),
            productPage.getSize()
        );
    }

    public static class ProductPageResult {
        private final List<Product> content;
        private final long totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;

        public ProductPageResult(List<Product> content, long totalElements, int totalPages, int currentPage, int pageSize) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }

        public List<Product> getContent() { return content; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
    }

    public static class ProductListItemPageResult {
        private final List<ProductListItemDto> content;
        private final long totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;

        public ProductListItemPageResult(List<ProductListItemDto> content, long totalElements, int totalPages, int currentPage, int pageSize) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }

        public List<ProductListItemDto> getContent() { return content; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
    }
}
