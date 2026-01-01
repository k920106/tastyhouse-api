package com.tastyhouse.core.repository.product;

import com.tastyhouse.core.entity.product.CommonOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonOptionJpaRepository extends JpaRepository<CommonOption, Long> {

    List<CommonOption> findByOptionGroupIdAndIsActiveTrueOrderBySortAsc(Long optionGroupId);

    List<CommonOption> findByOptionGroupIdInAndIsActiveTrueOrderBySortAsc(List<Long> optionGroupIds);
}
