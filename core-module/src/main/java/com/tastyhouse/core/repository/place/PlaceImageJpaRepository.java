package com.tastyhouse.core.repository.place;

import com.tastyhouse.core.entity.place.PlaceImage;
import com.tastyhouse.core.entity.place.PlaceImageCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceImageJpaRepository extends JpaRepository<PlaceImage, Long> {

    List<PlaceImage> findByPlaceIdAndIsThumbnailTrueOrderBySortAsc(Long placeId);

    Page<PlaceImage> findByPlaceIdAndImageCategoryOrderBySortAsc(Long placeId, PlaceImageCategory category, Pageable pageable);

    Page<PlaceImage> findByPlaceIdOrderBySortAsc(Long placeId, Pageable pageable);
}
