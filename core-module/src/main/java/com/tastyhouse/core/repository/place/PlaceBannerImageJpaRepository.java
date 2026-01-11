package com.tastyhouse.core.repository.place;

import com.tastyhouse.core.entity.place.PlaceBannerImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceBannerImageJpaRepository extends JpaRepository<PlaceBannerImage, Long> {

    Page<PlaceBannerImage> findByPlaceIdOrderBySortAsc(Long placeId, Pageable pageable);

    List<PlaceBannerImage> findByPlaceIdOrderBySortAsc(Long placeId);
}