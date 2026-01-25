package com.tastyhouse.core.repository.product;

import com.tastyhouse.core.entity.product.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageJpaRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdAndIsActiveTrueOrderBySortAsc(Long productId);

    List<ProductImage> findByProductIdOrderBySortAsc(Long productId);
}
