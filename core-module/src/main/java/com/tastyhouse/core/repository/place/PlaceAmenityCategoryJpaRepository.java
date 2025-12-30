package com.tastyhouse.core.repository.place;

import com.tastyhouse.core.entity.place.PlaceAmenityCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceAmenityCategoryJpaRepository extends JpaRepository<PlaceAmenityCategory, Long> {

    List<PlaceAmenityCategory> findAllByIsActiveTrueOrderBySortAsc();
}
