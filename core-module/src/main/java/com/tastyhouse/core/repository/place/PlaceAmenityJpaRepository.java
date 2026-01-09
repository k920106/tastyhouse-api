package com.tastyhouse.core.repository.place;

import com.tastyhouse.core.entity.place.PlaceAmenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceAmenityJpaRepository extends JpaRepository<PlaceAmenity, Long> {

    List<PlaceAmenity> findByPlaceId(Long placeId);
}
