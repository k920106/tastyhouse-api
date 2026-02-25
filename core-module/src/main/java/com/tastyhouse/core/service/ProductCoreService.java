package com.tastyhouse.core.service;

import com.tastyhouse.core.common.PageResult;
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

    public PageResult<TodayDiscountProductDto> findTodayDiscountProducts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<TodayDiscountProductDto> productPage = productRepository.findTodayDiscountProducts(pageRequest);
        return PageResult.from(productPage);
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

    public Optional<ProductOptionGroup> findProductOptionGroupById(Long optionGroupId) {
        return productOptionGroupJpaRepository.findById(optionGroupId);
    }

    public Optional<ProductOption> findProductOptionById(Long optionId) {
        return productOptionJpaRepository.findById(optionId);
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
}
