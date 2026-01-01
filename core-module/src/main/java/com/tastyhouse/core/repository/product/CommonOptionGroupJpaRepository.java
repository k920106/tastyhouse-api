package com.tastyhouse.core.repository.product;

import com.tastyhouse.core.entity.product.CommonOptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonOptionGroupJpaRepository extends JpaRepository<CommonOptionGroup, Long> {

    List<CommonOptionGroup> findByPlaceIdAndIsActiveTrueOrderBySortAsc(Long placeId);

    List<CommonOptionGroup> findByIdInAndIsActiveTrueOrderBySortAsc(List<Long> ids);
}
