package com.tastyhouse.core.repository.product;

import com.tastyhouse.core.entity.product.ProductBbq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductBbqJpaRepository extends JpaRepository<ProductBbq, Long> {

    Optional<ProductBbq> findByProductId(Long productId);

    boolean existsByProductId(Long productId);

    /**
     * 옵션 동기화가 완료되지 않은 ProductBbq 목록 조회
     */
    Optional<ProductBbq> findFirstByIsOptionsSyncedFalse();
}
