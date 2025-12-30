package com.tastyhouse.core.repository.place;

import com.tastyhouse.core.entity.place.PlaceFoodTypeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceFoodTypeCategoryJpaRepository extends JpaRepository<PlaceFoodTypeCategory, Long> {

    List<PlaceFoodTypeCategory> findAllByIsActiveTrueOrderBySortAsc();
}
