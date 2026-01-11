package com.tastyhouse.core.repository.place;

import com.tastyhouse.core.entity.place.PlacePhotoCategoryImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlacePhotoCategoryImageJpaRepository extends JpaRepository<PlacePhotoCategoryImage, Long> {

    Page<PlacePhotoCategoryImage> findByPlacePhotoCategoryIdOrderBySortAsc(Long placePhotoCategoryId, Pageable pageable);

    List<PlacePhotoCategoryImage> findByPlacePhotoCategoryIdOrderBySortAsc(Long placePhotoCategoryId);
}
