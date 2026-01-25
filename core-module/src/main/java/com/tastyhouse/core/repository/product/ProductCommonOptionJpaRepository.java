package com.tastyhouse.core.repository.product;

import com.tastyhouse.core.entity.product.ProductCommonOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCommonOptionJpaRepository extends JpaRepository<ProductCommonOption, Long> {

    List<ProductCommonOption> findByOptionGroupIdAndIsActiveTrueOrderBySortAsc(Long optionGroupId);

    List<ProductCommonOption> findByOptionGroupIdInAndIsActiveTrueOrderBySortAsc(List<Long> optionGroupIds);
}
