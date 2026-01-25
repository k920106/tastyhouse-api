package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.product.*;
import com.tastyhouse.core.entity.product.dto.TodayDiscountProductDto;
import com.tastyhouse.core.repository.product.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductCoreService {

    private final ProductRepository productRepository;
    private final ProductJpaRepository productJpaRepository;
    private final ProductCategoryJpaRepository productCategoryJpaRepository;
    private final ProductOptionGroupJpaRepository productOptionGroupJpaRepository;
    private final ProductOptionJpaRepository productOptionJpaRepository;
    private final ProductCommonOptionGroupJpaRepository productCommonOptionGroupJpaRepository;
    private final ProductCommonOptionJpaRepository productCommonOptionJpaRepository;

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

    public List<Product> findProductsByPlaceId(Long placeId) {
        return productJpaRepository.findByPlaceIdAndIsActiveTrueOrderByIsRepresentativeDescRatingDescIdAsc(placeId);
    }

    public List<Product> findActiveProductsByPlaceId(Long placeId) {
        return productJpaRepository.findByPlaceIdAndIsActiveTrueOrderBySortAsc(placeId);
    }

    public Optional<Product> findProductById(Long productId) {
        return productJpaRepository.findById(productId);
    }

    public Optional<ProductCategory> findProductCategoryById(Long categoryId) {
        return productCategoryJpaRepository.findById(categoryId);
    }

    public List<ProductCategory> findProductCategoriesByPlaceId(Long placeId) {
        return productCategoryJpaRepository.findByPlaceIdAndIsActiveTrueOrderBySortAsc(placeId);
    }

    public List<ProductOptionGroup> findProductOptionGroupsByProductId(Long productId) {
        return productOptionGroupJpaRepository.findByProductIdAndIsActiveTrueOrderBySortAsc(productId);
    }

    public List<ProductOption> findProductOptionsByOptionGroupIds(List<Long> optionGroupIds) {
        return productOptionJpaRepository.findByOptionGroupIdInAndIsActiveTrueOrderBySortAsc(optionGroupIds);
    }

    public List<ProductCommonOptionGroup> findProductCommonOptionGroupsByProductId(Long productId) {
        return productCommonOptionGroupJpaRepository.findByProductIdAndIsActiveTrueOrderBySortAsc(productId);
    }

    public List<ProductCommonOption> findProductCommonOptionsByOptionGroupIds(List<Long> optionGroupIds) {
        return productCommonOptionJpaRepository.findByOptionGroupIdInAndIsActiveTrueOrderBySortAsc(optionGroupIds);
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
